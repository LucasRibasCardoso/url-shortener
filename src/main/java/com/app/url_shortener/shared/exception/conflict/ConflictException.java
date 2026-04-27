package com.app.url_shortener.shared.exception.conflict;

import com.app.url_shortener.shared.exception.AppBusinessException;
import com.app.url_shortener.shared.exception.ErrorCode;

public abstract class ConflictException extends AppBusinessException {

  protected ConflictException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected ConflictException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }

}
