package com.app.url_shortener.user.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class InvalidUserPlanException extends DomainValidationException {

  public InvalidUserPlanException() {
    super(UserErrorCode.USER_PLAN_INVALID);
  }

}
