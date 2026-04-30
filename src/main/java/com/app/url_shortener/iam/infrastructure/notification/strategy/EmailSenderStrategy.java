package com.app.url_shortener.iam.infrastructure.notification.strategy;

public interface EmailSenderStrategy {

  void sendEmailVerificationCode(String email, String code);
}
