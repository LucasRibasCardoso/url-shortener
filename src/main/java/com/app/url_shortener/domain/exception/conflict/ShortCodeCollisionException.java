package com.app.url_shortener.domain.exception.conflict;

import com.app.url_shortener.domain.exception.ErrorCode;

public class ShortCodeCollisionException extends ConflictException {

  public ShortCodeCollisionException() {
    super(ErrorCode.URL_SHORT_CODE_COLLISION);
  }

  public ShortCodeCollisionException(Throwable cause) {
    super(ErrorCode.URL_SHORT_CODE_COLLISION, cause);
  }
}

