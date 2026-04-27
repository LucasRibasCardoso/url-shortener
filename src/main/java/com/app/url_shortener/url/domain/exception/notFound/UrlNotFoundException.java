package com.app.url_shortener.url.domain.exception.notFound;

import com.app.url_shortener.url.domain.exception.ErrorCode;

public class UrlNotFoundException extends NotFoundException {

  public UrlNotFoundException() {
    super(ErrorCode.URL_NOT_FOUND);
  }
}

