package com.app.url_shortener.iam.application.usecase.impl;

import com.app.url_shortener.iam.application.command.RefreshTokenCommand;
import com.app.url_shortener.iam.application.port.output.IssueAccessTokenPort;
import com.app.url_shortener.iam.application.port.output.RefreshTokenRepositoryPort;
import com.app.url_shortener.iam.application.port.output.SecureTokenGeneratorPort;
import com.app.url_shortener.iam.application.port.output.UserAccountRepositoryPort;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.iam.application.result.RefreshTokenResult;
import com.app.url_shortener.iam.application.usecase.RefreshTokenUseCase;
import com.app.url_shortener.iam.domain.exception.auth.RefreshTokenExpiredException;
import com.app.url_shortener.iam.domain.exception.auth.TokenCompromisedException;
import com.app.url_shortener.iam.domain.exception.user.UserNotFoundException;
import com.app.url_shortener.iam.domain.model.Permission;
import com.app.url_shortener.iam.domain.model.RefreshToken;
import com.app.url_shortener.iam.domain.model.Role;
import com.app.url_shortener.iam.domain.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

  private final RefreshTokenRepositoryPort tokenRepository;
  private final SecureTokenGeneratorPort tokenGenerator;
  private final IssueAccessTokenPort accessTokenPort;
  private final UserAccountRepositoryPort userRepository;

  @Override
  @Transactional
  public RefreshTokenResult execute(RefreshTokenCommand command) {
    RefreshToken oldToken = getAndValidateToken(command.refreshToken());

    String newRawToken = tokenGenerator.generateRandomToken();
    rotateAndSaveTokens(oldToken, newRawToken);

    AuthenticatedUserResult authenticatedUser = buildAuthenticatedUser(oldToken.getUserId());
    String newAccessToken = accessTokenPort.getToken(authenticatedUser);

    return new RefreshTokenResult(newRawToken, newAccessToken);
  }

  private RefreshToken getAndValidateToken(String rawToken) {
    String incomingHash = tokenGenerator.hashToken(rawToken);
    RefreshToken oldToken = tokenRepository.findByTokenHash(incomingHash)
            .orElseThrow(RefreshTokenExpiredException::new);

    if (oldToken.isRevoked()) {
      tokenRepository.revokeAllTokensForUser(oldToken.getUserId());
      throw new TokenCompromisedException();
    }

    return oldToken;
  }

  private void rotateAndSaveTokens(RefreshToken oldToken, String newRawToken) {
    String newHash = tokenGenerator.hashToken(newRawToken);
    RefreshToken newToken = oldToken.rotate(newHash);

    tokenRepository.save(newToken);
    tokenRepository.save(oldToken);
  }

  private AuthenticatedUserResult buildAuthenticatedUser(UUID userId) {
    UserAccount user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);

    List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();

    List<String> authorities = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .toList();

    return new AuthenticatedUserResult(
            user.getId(),
            user.getName(),
            user.getEmail(),
            roles,
            authorities,
            user.getPlan().name()
    );
  }
}
