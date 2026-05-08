package com.app.url_shortener.url.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class ShortCodeRequiredException extends DomainValidationException {

  public ShortCodeRequiredException() {
    super(UrlErrorCode.URL_SHORT_CODE_REQUIRED);
  }
}
