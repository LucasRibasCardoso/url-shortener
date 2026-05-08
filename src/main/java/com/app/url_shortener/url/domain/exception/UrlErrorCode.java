package com.app.url_shortener.url.domain.exception;

import com.app.url_shortener.shared.exception.ErrorCode;

public enum UrlErrorCode implements ErrorCode {
  URL_SHORT_CODE_REQUIRED("Codigo curto e obrigatorio."),
  URL_ORIGINAL_URL_REQUIRED("URL original e obrigatoria."),
  URL_SHORT_CODE_COLLISION("O codigo curto informado ja existe."),
  URL_NOT_FOUND("URL nao encontrada.");

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
