package com.app.url_shortener.auth.application.usecase.impl;

import com.app.url_shortener.auth.application.command.RegisterUserCommand;
import com.app.url_shortener.auth.application.port.output.AuthUserRepositoryPort;
import com.app.url_shortener.auth.application.port.output.EmailVerificationTokenPort;
import com.app.url_shortener.auth.application.port.output.PasswordEncoderPort;
import com.app.url_shortener.auth.application.result.RegisterUserResult;
import com.app.url_shortener.auth.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.auth.domain.event.EmailVerificationRequestedEvent;
import com.app.url_shortener.auth.domain.model.AuthUser;
import com.app.url_shortener.auth.domain.model.EmailVerificationToken;
import com.app.url_shortener.auth.domain.valueobject.VerificationCode;
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

  private final AuthUserRepositoryPort authUserRepository;
  private final PasswordEncoderPort passwordEncoder;
  private final EmailVerificationTokenPort emailVerificationTokenStore;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public RegisterUserResult execute(RegisterUserCommand command) {
    String email = normalizeEmail(command.email());
    String name = normalizeName(command.name());
    String passwordHash = passwordEncoder.encode(command.password());

    AuthUser authUser = AuthUser.createPendingRegistration(
            name,
            email,
            passwordHash);
    AuthUser savedUser = authUserRepository.save(authUser);

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
