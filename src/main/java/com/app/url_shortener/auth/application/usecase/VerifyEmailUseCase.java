package com.app.url_shortener.auth.application.usecase;

import com.app.url_shortener.auth.application.command.VerifyEmailCommand;
import com.app.url_shortener.auth.application.result.VerifyEmailResult;

public interface VerifyEmailUseCase {

  VerifyEmailResult execute(VerifyEmailCommand command);
}
