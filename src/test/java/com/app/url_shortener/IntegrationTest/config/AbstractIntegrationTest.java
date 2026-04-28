package com.app.url_shortener.IntegrationTest.config;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.testcontainers.utility.DockerImageName.parse;

import com.app.url_shortener.support.PostgresContainerSupport;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import java.net.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

  @LocalServerPort
  private int port;

  @BeforeEach
  void setupRestAssured() {
    RestAssured.port = this.port;
    RestAssured.config = RestAssuredConfig.config()
            .redirect(redirectConfig().followRedirects(false));
  }

  private static final DockerImageName REDIS_IMAGE = parse("redis:7-alpine");
  private static final DockerImageName LOCALSTACK_IMAGE = parse("localstack/localstack:3.0");

  protected static final GenericContainer<?> REDIS_CONTAINER =
      new GenericContainer<>(REDIS_IMAGE).withExposedPorts(6379);

  protected static final LocalStackContainer LOCALSTACK_CONTAINER =
      new LocalStackContainer(LOCALSTACK_IMAGE).withServices(LocalStackContainer.Service.DYNAMODB);

  static {
    REDIS_CONTAINER.start();
    LOCALSTACK_CONTAINER.start();
  }

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    PostgresContainerSupport.registerDatasourceProperties(registry);
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

    URI endpoint = LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB);

    registry.add("aws.dynamodb.endpoint", endpoint::toString);
    registry.add("aws.dynamodb.region", LOCALSTACK_CONTAINER::getRegion);
    registry.add("aws.dynamodb.access-key", () -> "test");
    registry.add("aws.dynamodb.secret-key", () -> "test");
  }

  @BeforeAll
  static void setupDynamoDbTable() {
    URI endpoint = LOCALSTACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB);
    String region = LOCALSTACK_CONTAINER.getRegion();

    DynamoDbClient dynamoDbClient =
        DynamoDbClient.builder()
            .endpointOverride(endpoint)
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
            .build();

    try {
      dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("url").build());
    } catch (ResourceNotFoundException e) {
      CreateTableRequest createRequest =
          CreateTableRequest.builder()
              .tableName("url")
              .attributeDefinitions(
                  AttributeDefinition.builder()
                      .attributeName("shortCode")
                      .attributeType(ScalarAttributeType.S)
                      .build())
              .keySchema(
                  KeySchemaElement.builder()
                      .attributeName("shortCode")
                      .keyType(KeyType.HASH)
                      .build())
              .billingMode(BillingMode.PAY_PER_REQUEST)
              .build();

      dynamoDbClient.createTable(createRequest);
    } finally {
      dynamoDbClient.close();
    }
  }
}
