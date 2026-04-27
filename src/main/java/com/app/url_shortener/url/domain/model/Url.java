package com.app.url_shortener.url.domain.model;

import com.app.url_shortener.url.domain.exception.validation.OriginalUrlRequiredException;
import com.app.url_shortener.url.domain.exception.validation.ShortCodeRequiredException;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Url implements Serializable {
  private final String shortCode;
  private final String originalUrl;
  private final LocalDateTime createdAt;

  private Url(String shortCode, String originalUrl, LocalDateTime createdAt) {
    if (shortCode == null || shortCode.isBlank()) {
      throw new ShortCodeRequiredException();
    }
    if (originalUrl == null || originalUrl.isBlank()) {
      throw new OriginalUrlRequiredException();
    }
    this.shortCode = shortCode.trim();
    this.originalUrl = originalUrl.trim();
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório");
  }

  public static Url create(String shortCode, String originalUrl) {
    return new Url(shortCode, originalUrl, LocalDateTime.now());
  }

  public static Url create(String shortCode, String originalUrl, LocalDateTime createdAt) {
    return new Url(shortCode, originalUrl, createdAt);
  }

}
