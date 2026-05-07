package com.app.url_shortener.iam.application.usecase.impl;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.port.output.AuthenticateCredentialsPort;
import com.app.url_shortener.iam.application.port.output.IssueAccessTokenPort;
import com.app.url_shortener.iam.application.port.output.RefreshTokenRepositoryPort;
import com.app.url_shortener.iam.application.port.output.SecureTokenGeneratorPort;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.application.usecase.LoginUseCase;
import com.app.url_shortener.iam.domain.exception.auth.InvalidCredentialsException;
import com.app.url_shortener.iam.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

  private static final String TOKEN_TYPE = "Bearer";

  private final IssueAccessTokenPort issueAccessTokenPort;
  private final AuthenticateCredentialsPort authenticateCredentialsPort;
  private final SecureTokenGeneratorPort tokenGenerator;
  private final RefreshTokenRepositoryPort tokenRepository;

  @Override
  @Transactional
  public LoginResult execute(LoginCommand command) {
    String email = command.email();
    if (email == null || email.isBlank()) {
      throw new InvalidCredentialsException();
    }

    AuthenticatedUserResult authenticatedUser = authenticateCredentialsPort.authenticate(email, command.password());

    String accessToken = issueAccessTokenPort.getToken(authenticatedUser);

    String rawRefreshToken = tokenGenerator.generateRandomToken();
    String tokenHash = tokenGenerator.hashToken(rawRefreshToken);
    RefreshToken refreshToken = RefreshToken.create(authenticatedUser.id(), tokenHash);
    tokenRepository.save(refreshToken);

    long expiresAt = issueAccessTokenPort.getExpiresInSeconds();

    return new LoginResult(rawRefreshToken, accessToken, TOKEN_TYPE, expiresAt, authenticatedUser);
  }
}
