package com.app.url_shortener.iam.application.usecase.impl;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.port.output.AuthenticateCredentialsPort;
import com.app.url_shortener.iam.application.port.output.IssueAccessTokenPort;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.application.usecase.LoginUseCase;
import com.app.url_shortener.iam.domain.exception.auth.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

  private static final String TOKEN_TYPE = "Bearer";

  private final IssueAccessTokenPort issueAccessTokenPort;
  private final AuthenticateCredentialsPort authenticateCredentialsPort;

  @Override
  @Transactional(readOnly = true)
  public LoginResult execute(LoginCommand command) {
    String email = normalizeEmail(command.email());

    AuthenticatedUserResult authenticatedUser = authenticateCredentialsPort.authenticate(email, command.password());

    return new LoginResult(
            issueAccessTokenPort.getToken(authenticatedUser),
            TOKEN_TYPE,
            issueAccessTokenPort.getExpiresInMinutes(),
            authenticatedUser
    );
  }

  private String normalizeEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new InvalidCredentialsException();
    }

    return email.trim().toLowerCase(Locale.ROOT);
  }
}
