package com.app.url_shortener.shared.exception.conflict;

import com.app.url_shortener.shared.exception.CommonErrorCode;

public class IdempotencyConflictException extends ConflictException {

  public IdempotencyConflictException() {
    super(CommonErrorCode.IDEMPOTENCY_IN_PROCESSING);
  }
}
