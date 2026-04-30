package com.app.url_shortener.auth.infrastructure.cache.redis;

import com.app.url_shortener.auth.application.port.output.EmailVerificationTokenPort;
import com.app.url_shortener.auth.domain.model.EmailVerificationToken;
import com.app.url_shortener.auth.domain.valueobject.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisEmailVerificationToken implements EmailVerificationTokenPort {

  private static final String KEY_PREFIX = "auth:email-verification:";
  private static final String VALUE_SEPARATOR = "|";

  private final StringRedisTemplate redisTemplate;

  @Override
  public void store(EmailVerificationToken token, Duration ttl) {
    String value = token.getUserId()
            + VALUE_SEPARATOR
            + token.getCode().value()
            + VALUE_SEPARATOR
            + token.getExpiresAt();

    redisTemplate.opsForValue().set(key(token.getEmail()), value, ttl);
  }

  @Override
  public Optional<EmailVerificationToken> findByEmail(String email) {
    String value = redisTemplate.opsForValue().get(key(email));
    if (value == null) {
      return Optional.empty();
    }

    String[] parts = value.split("\\|");
    if (parts.length != 3) {
      return Optional.empty();
    }

    return Optional.of(EmailVerificationToken.create(
            UUID.fromString(parts[0]),
            email,
            VerificationCode.of(parts[1]),
            Instant.parse(parts[2])));
  }

  @Override
  public void deleteByEmail(String email) {
    redisTemplate.delete(key(email));
  }


  private String key(String email) {
    return KEY_PREFIX + email;
  }
}
