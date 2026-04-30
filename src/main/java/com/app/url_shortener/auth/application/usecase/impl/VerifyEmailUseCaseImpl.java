package com.app.url_shortener.auth.application.usecase.impl;

import com.app.url_shortener.auth.application.command.VerifyEmailCommand;
import com.app.url_shortener.auth.application.port.output.AuthUserRepositoryPort;
import com.app.url_shortener.auth.application.port.output.EmailVerificationTokenPort;
import com.app.url_shortener.auth.application.port.output.RoleRepositoryPort;
import com.app.url_shortener.auth.application.result.VerifyEmailResult;
import com.app.url_shortener.auth.application.usecase.VerifyEmailUseCase;
import com.app.url_shortener.auth.domain.exception.EmailVerificationTokenExpiredException;
import com.app.url_shortener.auth.domain.exception.InvalidVerificationCodeException;
import com.app.url_shortener.auth.domain.model.EmailVerificationToken;
import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.auth.domain.valueobject.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCaseImpl implements VerifyEmailUseCase {

  private static final String SUCCESS_MESSAGE = "E-mail verificado com sucesso. Agora você pode fazer login na sua conta.";

  private final RoleRepositoryPort roleRepositoryPort;
  private final AuthUserRepositoryPort authUserRepositoryPort;
  private final EmailVerificationTokenPort emailVerificationTokenPort;

  @Override
  @Transactional
  public VerifyEmailResult execute(VerifyEmailCommand command) {
    String email = normalizeEmail(command.email());

    EmailVerificationToken token = validateEmailVerificationToken(emailVerificationTokenPort.findByEmail(email));
    validateEmailVerificationToken(token, command.code());

    Role defaultRole = roleRepositoryPort.findDefaultRole();
    authUserRepositoryPort.activateEmailVerification(email, defaultRole);

    registerTokenDeletionAfterCommit(email);
    return new VerifyEmailResult(SUCCESS_MESSAGE);
  }

  private void registerTokenDeletionAfterCommit(String email) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        emailVerificationTokenPort.deleteByEmail(email);
      }
    });
  }

  private void validateEmailVerificationToken(EmailVerificationToken token, VerificationCode code) {
    if (!token.matches(code)) {
      throw new InvalidVerificationCodeException();
    }
  }

  private EmailVerificationToken validateEmailVerificationToken(Optional<EmailVerificationToken> token) {
    if (token.isEmpty() || token.get().isExpired()) {
      throw new EmailVerificationTokenExpiredException();
    }

    return token.get();
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

}
