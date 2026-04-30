package com.app.url_shortener.iam.infrastructure.notification.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleEmailSenderStrategy implements EmailSenderStrategy {

  @Override
  public void sendEmailVerificationCode(String email, String code) {
    log.info("Código de verificação para {}: {}", email, code);
  }
}
