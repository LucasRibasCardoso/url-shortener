 package com.app.url_shortener.url.domain.exception.validation;

import com.app.url_shortener.url.domain.exception.ErrorCode;

public class ShortCodeRequiredException extends DomainValidationException {

  public ShortCodeRequiredException() {
    super(ErrorCode.URL_SHORT_CODE_REQUIRED);
  }

  public ShortCodeRequiredException(Throwable cause) {
    super(ErrorCode.URL_SHORT_CODE_REQUIRED, cause);
  }
}

