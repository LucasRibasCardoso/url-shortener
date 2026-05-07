package com.app.url_shortener.iam.application.command;

import java.util.Locale;

public record ResendVerificationCommand(String email) {
  public ResendVerificationCommand {
    email = email != null ? email.trim().toLowerCase(Locale.ROOT) : null;
  }
}
