package com.app.url_shortener.domain.exception;

public enum ErrorCode {
  URL_SHORT_CODE_REQUIRED("O codigo curto e obrigatorio."),
  URL_ORIGINAL_URL_REQUIRED("A URL original e obrigatoria."),
  URL_SHORT_CODE_COLLISION("O codigo curto informado ja existe."),
  URL_NOT_FOUND("URL nao encontrada."),
  URL_REQUEST_VALIDATION_FAILED("Um ou mais campos estao invalidos."),
  URL_BUSINESS_RULE_VIOLATION("Regra de negocio violada."),
  DEPENDENCY_FAILURE("Falha temporaria em servico de infraestrutura."),
  INTERNAL_SERVER_ERROR("Erro interno inesperado.");

  private final String message;

  ErrorCode(String message) {
	this.message = message;
  }

  public String getMessage() {
	return message;
  }
}
