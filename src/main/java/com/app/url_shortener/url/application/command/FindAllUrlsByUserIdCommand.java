package com.app.url_shortener.url.application.command;

import java.util.UUID;

public record FindAllUrlsByUserIdCommand(UUID userId, int limit, String cursor) {

  public FindAllUrlsByUserIdCommand {
    cursor = cursor != null ? cursor.trim() : null;
  }
}
