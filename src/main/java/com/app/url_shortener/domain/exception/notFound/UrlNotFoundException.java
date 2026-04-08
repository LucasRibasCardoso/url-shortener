package com.app.url_shortener.domain.exception.notFound;

import com.app.url_shortener.domain.exception.ErrorCode;

public class UrlNotFoundException extends NotFoundException {

  public UrlNotFoundException() {
    super(ErrorCode.URL_NOT_FOUND);
  }
}

