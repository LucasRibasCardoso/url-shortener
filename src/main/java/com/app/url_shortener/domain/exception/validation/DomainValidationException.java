package com.app.url_shortener.domain.exception.validation;

import com.app.url_shortener.domain.exception.AppBusinessException;
import com.app.url_shortener.domain.exception.ErrorCode;

public abstract class DomainValidationException extends AppBusinessException {

  protected DomainValidationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

  protected DomainValidationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
