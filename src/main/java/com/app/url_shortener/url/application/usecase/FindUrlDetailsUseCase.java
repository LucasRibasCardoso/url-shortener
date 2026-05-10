package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.application.command.UrlDetailsCommand;
import com.app.url_shortener.url.application.result.UrlDetailsResult;

public interface FindUrlDetailsUseCase {
  UrlDetailsResult execute(UrlDetailsCommand command);
}
