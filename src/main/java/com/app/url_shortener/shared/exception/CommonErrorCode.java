package com.app.url_shortener.shared.exception;

public enum CommonErrorCode implements ErrorCode {
  REQUEST_VALIDATION_FAILED("Um ou mais campos estão inválidos."),
  DEPENDENCY_FAILURE("Falha temporária em serviço de infraestrutura."),
  INTERNAL_SERVER_ERROR("Erro interno inesperado."),
  IDEMPOTENCY_HEADER_MISSING("O cabeçalho Idempotency-Key é obrigatório para esta operação."),
  IDEMPOTENCY_IN_PROCESSING("A requisição já está em processamento. Aguarde."),
  AUTH_ACCESS_DENIED("Acesso negado para esse recurso"),
  AUTH_UNAUTHORIZED("Autenticação necessária ou token inválido.")
  ;

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
