package com.app.url_shortener.shared.exception.validation;

import com.app.url_shortener.shared.exception.AppBusinessException;
import com.app.url_shortener.shared.exception.ErrorCode;

public abstract class DomainValidationException extends AppBusinessException {

  protected DomainValidationException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected DomainValidationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
