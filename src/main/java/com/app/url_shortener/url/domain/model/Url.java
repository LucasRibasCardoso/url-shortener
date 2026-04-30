package com.app.url_shortener.url.domain.model;

import com.app.url_shortener.url.domain.exception.OriginalUrlRequiredException;
import com.app.url_shortener.url.domain.exception.ShortCodeRequiredException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public class Url implements Serializable {
  private final String shortCode;
  private final String originalUrl;
  private final LocalDateTime createdAt;

  private Url(String shortCode, String originalUrl, LocalDateTime createdAt) {
    this.shortCode = validateShortCode(shortCode);
    this.originalUrl = validateOriginalUrl(originalUrl);
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório");
  }

  public static Url create(String shortCode, String originalUrl) {
    return new Url(shortCode, originalUrl, LocalDateTime.now());
  }

  public static Url restore(String shortCode, String originalUrl, LocalDateTime createdAt) {
    return new Url(shortCode, originalUrl, createdAt);
  }

  private static String validateShortCode(String shortCode) {
    if (shortCode == null || shortCode.isBlank()) {
      throw new ShortCodeRequiredException();
    }

    return shortCode.trim();
  }

  private static String validateOriginalUrl(String originalUrl) {
    if (originalUrl == null || originalUrl.isBlank()) {
      throw new OriginalUrlRequiredException();
    }

    return originalUrl.trim();
  }

}
