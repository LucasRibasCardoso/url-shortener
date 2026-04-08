package com.app.url_shortener.application.usecase;

import com.app.url_shortener.domain.model.Url;

public interface ShortenUrlUseCase {
  Url execute(String originalUrl);
}
