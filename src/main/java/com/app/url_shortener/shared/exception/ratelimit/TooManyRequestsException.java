package com.app.url_shortener.shared.exception.ratelimit;

import com.app.url_shortener.shared.exception.AppBusinessException;
import com.app.url_shortener.shared.exception.ErrorCode;

public abstract class TooManyRequestsException extends AppBusinessException {

  protected TooManyRequestsException(ErrorCode errorCode) {
	super(errorCode);
  }

  protected TooManyRequestsException(ErrorCode errorCode, Throwable cause) {
	super(errorCode, cause);
  }
}
