package com.app.url_shortener.url.domain.exception;

import com.app.url_shortener.shared.exception.ErrorCode;

public enum UrlErrorCode implements ErrorCode {
  URL_SHORT_CODE_REQUIRED("O codigo curto e obrigatorio."),
  URL_ORIGINAL_URL_REQUIRED("A URL original e obrigatoria."),
  URL_SHORT_CODE_COLLISION("O codigo curto informado ja existe."),
  URL_NOT_FOUND("URL nao encontrada."),
  URL_REQUEST_VALIDATION_FAILED("Um ou mais campos estao invalidos."),
  URL_BUSINESS_RULE_VIOLATION("Regra de negocio violada.");

  private final String message;

  UrlErrorCode(String message) {
	this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
	return message;
  }
}
