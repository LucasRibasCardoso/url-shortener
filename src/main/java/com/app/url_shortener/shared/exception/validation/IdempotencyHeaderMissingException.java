package com.app.url_shortener.shared.exception.validation;

import com.app.url_shortener.shared.exception.CommonErrorCode;

public class IdempotencyHeaderMissingException extends DomainValidationException {

  public IdempotencyHeaderMissingException() {
    super(CommonErrorCode.IDEMPOTENCY_HEADER_MISSING);
  }
}
