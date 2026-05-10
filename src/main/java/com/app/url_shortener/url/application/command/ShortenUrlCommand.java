package com.app.url_shortener.url.application.command;

import java.util.UUID;

public record ShortenUrlCommand(UUID userId, String originalUrl) {

  public ShortenUrlCommand {
    originalUrl = originalUrl != null ? originalUrl.trim() : null;
  }
}
