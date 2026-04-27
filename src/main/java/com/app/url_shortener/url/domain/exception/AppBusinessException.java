package com.app.url_shortener.url.domain.exception;

import java.util.Objects;

public abstract class AppBusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  protected AppBusinessException(ErrorCode errorCode) {
    super(Objects.requireNonNull(errorCode, "errorCode is required").getMessage());
    this.errorCode = errorCode;
  }

  protected AppBusinessException(ErrorCode errorCode, Throwable cause) {
    super(Objects.requireNonNull(errorCode, "errorCode is required").getMessage(), cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}

