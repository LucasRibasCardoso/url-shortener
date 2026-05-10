package com.app.url_shortener.iam.domain.exception.user;

import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class UserAccountDisabledException extends ConflictException {
    
    public UserAccountDisabledException() {
        super(UserErrorCode.USER_ACCOUNT_DISABLED);
    }
}
