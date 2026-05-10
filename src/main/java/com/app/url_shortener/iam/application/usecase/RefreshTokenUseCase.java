package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.RefreshTokenCommand;
import com.app.url_shortener.iam.application.result.RefreshTokenResult;

public interface RefreshTokenUseCase {
  RefreshTokenResult execute(RefreshTokenCommand command);
}
