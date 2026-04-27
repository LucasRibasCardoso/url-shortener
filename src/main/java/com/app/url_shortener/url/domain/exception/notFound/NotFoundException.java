package com.app.url_shortener.url.domain.exception.notFound;

import com.app.url_shortener.url.domain.exception.AppBusinessException;
import com.app.url_shortener.url.domain.exception.ErrorCode;

public abstract class NotFoundException extends AppBusinessException {

  protected NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected NotFoundException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}

