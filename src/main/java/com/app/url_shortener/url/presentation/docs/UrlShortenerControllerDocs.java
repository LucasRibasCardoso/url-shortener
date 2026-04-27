package com.app.url_shortener.url.presentation.docs;

import com.app.url_shortener.url.presentation.dto.request.ShortenUrlRequest;
import com.app.url_shortener.url.presentation.dto.response.UrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Encurtador", description = "Endpoints para gerenciamento de URLs curtas")
public interface UrlShortenerControllerDocs {

  @Operation(
      summary = "Encurtar URL longa",
      description = "Recebe uma URL longa válida e retorna a URL encurtada correspondente.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "URL curta criada com sucesso"),
    @ApiResponse(
        responseCode = "400",
        description = "Erro de validação no payload da requisição",
        content =
            @Content(
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                  @ExampleObject(
                      name = "Erro de validação de payload",
                      value =
                          """
                          {
                            "type": "/errors/validation",
                            "title": "Validação",
                            "status": 400,
                            "detail": "Um ou mais campos estao invalidos.",
                            "instance": "/api/v1/shorten",
                            "errorCode": "URL_REQUEST_VALIDATION_FAILED",
                            "errors": [
                              {
                                "field": "originalUrl",
                                "message": "A URL não pode estar vazia"
                              }
                            ]
                          }
                          """)
                })),
    @ApiResponse(
        responseCode = "409",
        description = "Conflito: short code já existente",
        content =
            @Content(
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                  @ExampleObject(
                      name = "Erro de conflito de short code",
                      value =
                          """
                          {
                            "type": "/errors/conflict",
                            "title": "Conflito",
                            "status": 409,
                            "detail": "O codigo curto informado ja existe.",
                            "instance": "/api/v1/shorten",
                            "errorCode": "URL_SHORT_CODE_COLLISION"
                          }
                          """)
                }))
  })
  ResponseEntity<@NonNull UrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request);
}
