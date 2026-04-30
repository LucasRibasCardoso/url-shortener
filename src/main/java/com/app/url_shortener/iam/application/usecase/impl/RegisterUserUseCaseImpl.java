package com.app.url_shortener.iam.application.usecase.impl;

import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.port.output.UserAccountRepositoryPort;
import com.app.url_shortener.iam.application.port.output.EmailVerificationTokenPort;
import com.app.url_shortener.iam.application.port.output.PasswordEncoderPort;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.iam.domain.event.EmailVerificationRequestedEvent;
import com.app.url_shortener.iam.domain.model.UserAccount;
import com.app.url_shortener.iam.domain.valueobject.EmailVerificationToken;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

  private static final String SUCCESS_MESSAGE = "Conta criada com sucesso. Enviamos um código de verificação para o seu e-mail.";
  private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(10);
  private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

  private final UserAccountRepositoryPort userAccountRepositoryPort;
  private final PasswordEncoderPort passwordEncoder;
  private final EmailVerificationTokenPort emailVerificationTokenStore;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public RegisterUserResult execute(RegisterUserCommand command) {
    String email = normalizeEmail(command.email());
    String name = normalizeName(command.name());
    String passwordHash = passwordEncoder.encode(command.password());

    UserAccount userAccount = UserAccount.createPendingRegistration(
            name,
            email,
            passwordHash);
    UserAccount savedUser = userAccountRepositoryPort.saveNewUserAccount(userAccount);

    VerificationCode code = VerificationCode.generate();
    EmailVerificationToken token = EmailVerificationToken.create(
            savedUser.getId(),
            savedUser.getEmail(),
            code,
            Instant.now().plus(VERIFICATION_CODE_TTL));

    emailVerificationTokenStore.store(token, VERIFICATION_CODE_TTL);
    eventPublisher.publishEvent(new EmailVerificationRequestedEvent(savedUser.getId(), savedUser.getEmail(), code));

    return new RegisterUserResult(SUCCESS_MESSAGE);
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeName(String name) {
    return MULTIPLE_SPACES.matcher(name.trim()).replaceAll(" ");
  }
}
