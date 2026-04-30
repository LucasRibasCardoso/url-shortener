package com.app.url_shortener.auth.infrastructure.notification.strategy;

public interface EmailSenderStrategy {

  void sendEmailVerificationCode(String email, String code);
}
