package com.app.url_shortener.shared.exception;

public enum CommonErrorCode implements ErrorCode {
  REQUEST_VALIDATION_FAILED("Um ou mais campos estão inválidos."),
  DEPENDENCY_FAILURE("Falha temporária em serviço de infraestrutura."),
  INTERNAL_SERVER_ERROR("Erro interno inesperado.");

  private final String message;

  CommonErrorCode(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getCode() {
    return name();
  }
}
