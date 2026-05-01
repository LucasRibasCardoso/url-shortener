package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.forbidden.ForbiddenException;

public class AccountLockedException extends ForbiddenException {
    public AccountLockedException() {
        super(AuthErrorCode.AUTH_ACCOUNT_LOCKED);
    }
}
