package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;

public interface VerifyEmailUseCase {

  VerifyEmailResult execute(VerifyEmailCommand command);
}
