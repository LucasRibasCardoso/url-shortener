package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.result.RegisterUserResult;

public interface RegisterUserUseCase {

  RegisterUserResult execute(RegisterUserCommand command);
}
