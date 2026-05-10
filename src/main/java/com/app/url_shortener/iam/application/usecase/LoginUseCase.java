package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.result.LoginResult;

public interface LoginUseCase {

  LoginResult execute(LoginCommand command);
}
