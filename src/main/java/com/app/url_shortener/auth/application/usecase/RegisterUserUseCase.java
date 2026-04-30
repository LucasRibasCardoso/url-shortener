package com.app.url_shortener.auth.application.usecase;

import com.app.url_shortener.auth.application.command.RegisterUserCommand;
import com.app.url_shortener.auth.application.result.RegisterUserResult;

public interface RegisterUserUseCase {

  RegisterUserResult execute(RegisterUserCommand command);
}
