package com.app.url_shortener.url.application.command;

import java.util.UUID;

public record DeleteUrlCommand(UUID requesterId, String shortCode, boolean canDeleteAny) {

  public DeleteUrlCommand {
    shortCode = shortCode != null ? shortCode.trim() : null;
  }
}
