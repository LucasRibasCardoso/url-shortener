package com.app.url_shortener.url.presentation.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.app.url_shortener.config.AbstractIntegrationTest;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.domain.exception.ShortCodeCollisionException;
import com.app.url_shortener.url.presentation.dto.request.ShortenUrlRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

@DisplayName("Integration Tests - URL Shortener API")
@Disabled("Testes de integração serão corrigidos mais tarde")
public class UrlShortenerIT extends AbstractIntegrationTest {

  @Autowired private DynamoDbClient dynamoDbClient;

  @Value("${aws.dynamodb.table-name}")
  private String tableName;

  @Nested
  @DisplayName("Cenários de Sucesso (End-to-End)")
  class HappyPathScenarios {

    @Test
    @DisplayName("Deve encurtar uma URL e realizar o redirecionamento com sucesso")
    void shouldShortenUrlAndRedirectSuccessfully() {
      // Arrange
      ShortenUrlRequest request = new ShortenUrlRequest("https://www.google.com");

      // Act
      Response shortenResponse =
          given()
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .post("/api/v1/shorten")
              .then()
              .statusCode(201)
              .extract()
              .response();

      // Assert
      String shortUrl = shortenResponse.jsonPath().getString("shortUrl");
      assertThat(shortUrl).isNotBlank();
      String shortCode = extractShortCode(shortUrl);

      // Act
      Response redirectResponse =
          given().when().get("/" + shortCode).then().statusCode(302).extract().response();

      // Assert
      String locationHeader = redirectResponse.getHeader("Location");
      assertThat(locationHeader).isEqualTo(request.originalUrl());
    }
  }

  @Nested
  @DisplayName("Cenários de Erros 400")
  class ValidationScenarios {

    @DisplayName("Deve retornar 400 quando a originalUrl for inválida")
    @ParameterizedTest(name = "originalUrl == {0}")
    @MethodSource("invalidRequests")
    void shouldReturnBadRequestForInvalidOriginalUrl(ShortenUrlRequest invalidRequest) {
      // Arrange

      // Act
      Response response =
          given()
              .contentType(ContentType.JSON)
              .body(invalidRequest)
              .when()
              .post("/api/v1/shorten")
              .then()
              .statusCode(400)
              .extract()
              .response();

      // Assert
      response.then().body("$", hasKey("title"));
      response.then().body("status", equalTo(400));
      response.then().body("title", anyOf(equalTo("Bad Request"), equalTo("Validação")));
    }

    private static Stream<Arguments> invalidRequests() {
      return Stream.of(
          Arguments.of("nulo", new ShortenUrlRequest(null)),
          Arguments.of("vazio", new ShortenUrlRequest("")),
          Arguments.of("inválido", new ShortenUrlRequest("apenas-uma-string-qualquer")));
    }
  }

  @Nested
  @DisplayName("Cenários de Erros 404")
  class NotFoundScenarios {
    @Test
    @DisplayName("Deve retornar 404 quando o shortCode não existe")
    void shouldReturnNotFoundWhenShortCodeDoesNotExist() {
      // Arrange
      String invalidShortCode = "codigoFake123";

      // Act
      Response response =
          given().when().get("/" + invalidShortCode).then().statusCode(404).extract().response();

      // Assert
      response.then().body("title", equalTo("Não encontrado"));
    }
  }

  @Nested
  @DisplayName("Cenários de Error 409")
  class BusinessRulesScenarios {

    @MockitoBean private UrlRepositoryPort urlRepositoryPortMock;

    @Test
    @DisplayName("Deve retornar 409 se ocorrer uma colisão interna de shortCode gerado")
    void shouldReturnConflictWhenInternalCollisionOccurs() {
      // Arrange
      doThrow(new ShortCodeCollisionException()).when(urlRepositoryPortMock).save(any());

      ShortenUrlRequest request = new ShortenUrlRequest("https://www.google.com");

      // Act
      Response response =
          given()
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .post("/api/v1/shorten")
              .then()
              .statusCode(409)
              .extract()
              .response();

      // Assert: Valida a estrutura da sua mensagem de erro padrão
      response.then().body("title", equalTo("Conflito"));
    }
  }

  @Nested
  @DisplayName("Cenários de Performance e Cache")
  class PerformanceScenarios {

    @Test
    @DisplayName("Deve servir do cache após a primeira requisição")
    void shouldServeFromCacheAfterFirstRequest() {
      // Arrange
      ShortenUrlRequest request = new ShortenUrlRequest("https://www.google.com");

      Response shortenResponse =
          given()
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .post("/api/v1/shorten")
              .then()
              .statusCode(201)
              .extract()
              .response();

      String shortUrl = shortenResponse.jsonPath().getString("shortUrl");
      String shortCode = extractShortCode(shortUrl);

      // Act - Primeira requisição: carrega do DynamoDB e cachea no Redis
      Response firstRedirectResponse =
          given().when().get("/" + shortCode).then().statusCode(302).extract().response();

      // Assert - Valida a primeira resposta
      String firstLocationHeader = firstRedirectResponse.getHeader("Location");
      assertThat(firstLocationHeader).isEqualTo(request.originalUrl());

      // Arrange - Simula falha do DynamoDB deletando o registro original
      deleteFromDynamoDb(shortCode);

      // Act - Segunda requisição: deve vir do cache Redis mesmo com DynamoDB vazio
      Response secondRedirectResponse =
          given().when().get("/" + shortCode).then().statusCode(302).extract().response();

      // Assert - Valida que o cache contém a resposta correta
      String secondLocationHeader = secondRedirectResponse.getHeader("Location");
      assertThat(secondLocationHeader).isEqualTo(request.originalUrl());
    }
  }

  private String extractShortCode(String shortUrl) {
    int lastSlashIndex = shortUrl.lastIndexOf('/');
    return lastSlashIndex >= 0 ? shortUrl.substring(lastSlashIndex + 1) : shortUrl;
  }

  private void deleteFromDynamoDb(String shortCode) {
    Map<String, AttributeValue> key =
        Map.of("shortCode", AttributeValue.builder().s(shortCode).build());

    DeleteItemRequest request = DeleteItemRequest.builder().tableName(tableName).key(key).build();
    dynamoDbClient.deleteItem(request);
  }
}
