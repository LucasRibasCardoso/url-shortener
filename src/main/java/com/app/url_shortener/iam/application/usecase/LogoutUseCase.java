package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.LogoutCommand;

public interface LogoutUseCase {
  void execute(LogoutCommand command);
}
