package com.app.url_shortener.url.application.command;

public record ResolveUrlCommand(String shortCode) {

  public ResolveUrlCommand {
    shortCode = shortCode != null ? shortCode.trim() : null;
  }
}
