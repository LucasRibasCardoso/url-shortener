package com.app.url_shortener.shared.exception.conflict;

import com.app.url_shortener.shared.exception.CommonErrorCode;

public class DataIntegrityConflictException extends ConflictException {

  public DataIntegrityConflictException() {
    super(CommonErrorCode.DATA_INTEGRITY_CONFLICT);
  }
}
