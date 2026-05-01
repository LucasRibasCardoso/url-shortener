package com.app.url_shortener.url.presentation.controller;

import com.app.url_shortener.url.application.usecase.ShortenUrlUseCase;
import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.presentation.docs.UrlShortenerControllerDocs;
import com.app.url_shortener.url.presentation.dto.request.ShortenUrlRequest;
import com.app.url_shortener.url.presentation.dto.response.UrlResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shorten")
public class UrlShortenerController implements UrlShortenerControllerDocs {

  private final String baseUrl;
  private final ShortenUrlUseCase shortenUrlUseCase;

  public UrlShortenerController(
      ShortenUrlUseCase shortenUrlUseCase,
      @Value("${app.base-url}") String baseUrl) {
    this.shortenUrlUseCase = shortenUrlUseCase;
    this.baseUrl = baseUrl;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('url:create')")
  public ResponseEntity<@NonNull UrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
    Url createdUrl = shortenUrlUseCase.execute(request.originalUrl());
    String fullUrl = concatFullUrl(createdUrl);

    UrlResponse response =
        new UrlResponse(createdUrl.getOriginalUrl(), fullUrl, createdUrl.getCreatedAt());

    return ResponseEntity.created(URI.create(fullUrl)).body(response);
  }

  private String concatFullUrl(Url url) {
    return baseUrl.endsWith("/")
        ? baseUrl + "r/" + url.getShortCode()
        : baseUrl + "/r/" + url.getShortCode();
  }
}
