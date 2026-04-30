package com.app.url_shortener.auth.infrastructure.event.listener;

import com.app.url_shortener.auth.domain.event.EmailVerificationRequestedEvent;
import com.app.url_shortener.auth.infrastructure.notification.strategy.EmailSenderStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailVerificationEventListener {

  private final EmailSenderStrategy emailSenderStrategy;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onEmailVerificationRequested(EmailVerificationRequestedEvent event) {
    emailSenderStrategy.sendEmailVerificationCode(event.email(), event.code().value());
  }
}
