package com.app.url_shortener.url.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Redirecionamento", description = "Endpoint para redirecionar URL curta para a URL original")
public interface RedirectControllerDocs {

  @Operation(
      summary = "Redirecionar URL curta",
      description = "Recebe um short code e retorna redirecionamento HTTP para a URL original cadastrada.")
  @ApiResponses({
    @ApiResponse(responseCode = "302", description = "Redirecionamento realizado com sucesso"),
    @ApiResponse(
        responseCode = "404",
        description = "Short code não encontrado",
        content =
            @Content(
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                  @ExampleObject(
                      name = "Erro de recurso não encontrado",
                      value =
                          """
                          {
                            "type": "/errors/not-found",
                            "title": "Não encontrado",
                            "status": 404,
                            "detail": "URL nao encontrada.",
                            "instance": "/{shortCode}",
                            "errorCode": "URL_NOT_FOUND"
                          }
                          """)
                })),
    @ApiResponse(
        responseCode = "422",
        description = "Erro de regra de negócio",
        content =
            @Content(
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                  @ExampleObject(
                      name = "Erro de regra de negócio",
                      value =
                          """
                          {
                            "type": "/errors/business",
                            "title": "Negócio",
                            "status": 422,
                            "detail": "Regra de negocio violada.",
                            "instance": "/{shortCode}",
                            "errorCode": "URL_BUSINESS_RULE_VIOLATION"
                          }
                          """)
                }))
  })
  ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortCode);
}
