package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.application.command.DeleteUrlCommand;

public interface DeleteUrlUseCase {
  void execute(DeleteUrlCommand command);
}
