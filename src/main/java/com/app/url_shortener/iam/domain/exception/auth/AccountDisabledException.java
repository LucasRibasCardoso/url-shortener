package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.forbidden.ForbiddenException;

public class AccountDisabledException extends ForbiddenException {
    public AccountDisabledException() {
        super(AuthErrorCode.AUTH_ACCOUNT_PENDING_VERIFICATION);
    }
}
