package com.app.url_shortener.url.domain.exception.validation;

import com.app.url_shortener.url.domain.exception.ErrorCode;

public class OriginalUrlRequiredException extends DomainValidationException {

  public OriginalUrlRequiredException() {
    super(ErrorCode.URL_ORIGINAL_URL_REQUIRED);
  }

  public OriginalUrlRequiredException(Throwable cause) {
    super(ErrorCode.URL_ORIGINAL_URL_REQUIRED, cause);
  }
}

