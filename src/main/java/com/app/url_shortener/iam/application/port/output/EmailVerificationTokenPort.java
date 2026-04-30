package com.app.url_shortener.iam.application.port.output;

import com.app.url_shortener.iam.domain.valueobject.EmailVerificationToken;

import java.time.Duration;
import java.util.Optional;

public interface EmailVerificationTokenPort {

  void store(EmailVerificationToken token, Duration ttl);

  Optional<EmailVerificationToken> findByEmail(String email);

  void deleteByEmail(String email);
}
