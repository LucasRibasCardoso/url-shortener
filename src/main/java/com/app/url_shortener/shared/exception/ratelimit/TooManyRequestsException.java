package com.app.url_shortener.shared.exception.ratelimit;

import com.app.url_shortener.shared.exception.AppBusinessException;
import com.app.url_shortener.shared.exception.ErrorCode;

public abstract class TooManyRequestsException extends AppBusinessException {

  public TooManyRequestsException(ErrorCode errorCode) {
	super(errorCode);
  }

}
