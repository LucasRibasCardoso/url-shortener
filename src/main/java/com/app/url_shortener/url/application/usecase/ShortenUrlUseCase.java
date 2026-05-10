package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.application.command.ShortenUrlCommand;
import com.app.url_shortener.url.application.result.ShortenUrlResult;

public interface ShortenUrlUseCase {
  ShortenUrlResult execute(ShortenUrlCommand command);
}
