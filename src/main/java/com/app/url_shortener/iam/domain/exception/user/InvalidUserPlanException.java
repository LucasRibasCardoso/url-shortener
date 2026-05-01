package com.app.url_shortener.iam.domain.exception.user;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class InvalidUserPlanException extends DomainValidationException {

  public InvalidUserPlanException() {
    super(UserErrorCode.USER_PLAN_INVALID);
  }

}
