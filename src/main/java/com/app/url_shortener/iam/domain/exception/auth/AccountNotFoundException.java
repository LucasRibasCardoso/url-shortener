package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.forbidden.ForbiddenException;

public class AccountNotFoundException extends ForbiddenException {
    public AccountNotFoundException() {
        super(AuthErrorCode.AUTH_ACCOUNT_NOT_FOUND);
    }
}
