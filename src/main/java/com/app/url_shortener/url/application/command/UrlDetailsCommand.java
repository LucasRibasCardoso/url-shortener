package com.app.url_shortener.url.application.command;

import java.util.UUID;

public record UrlDetailsCommand(UUID requesterId, String shortCode, boolean canReadAny) {

  public UrlDetailsCommand {
    shortCode = shortCode != null ? shortCode.trim() : null;
  }
}
