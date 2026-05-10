package com.app.url_shortener.shared.exception.internalservererror;

public class IdempotencyCacheException extends RuntimeException {

  public IdempotencyCacheException(String message) {
    super(message);
  }
}
