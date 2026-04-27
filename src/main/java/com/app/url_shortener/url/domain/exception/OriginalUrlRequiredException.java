package com.app.url_shortener.url.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class OriginalUrlRequiredException extends DomainValidationException {

  public OriginalUrlRequiredException() {
    super(UrlErrorCode.URL_ORIGINAL_URL_REQUIRED);
  }

  public OriginalUrlRequiredException(Throwable cause) {
    super(UrlErrorCode.URL_ORIGINAL_URL_REQUIRED, cause);
  }
}

