package com.app.url_shortener.url.application.usecase.impl;

import com.app.url_shortener.url.application.command.ResolveUrlCommand;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.application.result.ResolvedUrlResult;
import com.app.url_shortener.url.application.usecase.ResolveUrlUseCase;
import com.app.url_shortener.url.domain.exception.UrlNotFoundException;
import com.app.url_shortener.url.domain.model.Url;
import org.springframework.stereotype.Service;

@Service
public class ResolveUrlUseCaseImpl implements ResolveUrlUseCase {

  private final UrlRepositoryPort urlRepositoryPort;

  public ResolveUrlUseCaseImpl(UrlRepositoryPort urlRepositoryPort) {
    this.urlRepositoryPort = urlRepositoryPort;
  }

  @Override
  public ResolvedUrlResult execute(ResolveUrlCommand command) {
    String originalUrl = urlRepositoryPort
        .findByShortCode(command.shortCode())
        .map(Url::getOriginalUrl)
        .orElseThrow(UrlNotFoundException::new);

    return new ResolvedUrlResult(originalUrl);
  }
}
