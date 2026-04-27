package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.domain.model.Url;

public interface ShortenUrlUseCase {
  Url execute(String originalUrl);
}
