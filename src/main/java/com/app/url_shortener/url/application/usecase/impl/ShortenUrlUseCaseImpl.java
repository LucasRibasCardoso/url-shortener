package com.app.url_shortener.url.application.usecase.impl;

import com.app.url_shortener.url.application.command.ShortenUrlCommand;
import com.app.url_shortener.url.application.port.output.UrlEncoderPort;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.application.port.output.IdGeneratorPort;
import com.app.url_shortener.url.application.result.ShortenUrlResult;
import com.app.url_shortener.url.application.usecase.ShortenUrlUseCase;
import com.app.url_shortener.url.domain.model.Url;
import org.springframework.stereotype.Service;

@Service
public class ShortenUrlUseCaseImpl implements ShortenUrlUseCase {

  private final IdGeneratorPort idGeneratorService;
  private final UrlRepositoryPort urlRepositoryPort;
  private final UrlEncoderPort urlEncoderPort;

  public ShortenUrlUseCaseImpl(
      IdGeneratorPort idGeneratorService,
      UrlRepositoryPort urlRepositoryPort,
      UrlEncoderPort urlEncoderPort) {
    this.idGeneratorService = idGeneratorService;
    this.urlRepositoryPort = urlRepositoryPort;
    this.urlEncoderPort = urlEncoderPort;
  }

  @Override
  public ShortenUrlResult execute(ShortenUrlCommand command) {
    long uniqueId = idGeneratorService.generateId();
    String shortCode = urlEncoderPort.encode(uniqueId);

    Url url = Url.create(command.userId(), shortCode, command.originalUrl());
    urlRepositoryPort.save(url);

    return toResult(url);
  }

  private ShortenUrlResult toResult(Url url) {
    return new ShortenUrlResult(url.getOriginalUrl(), url.getShortCode(), url.getCreatedAt());
  }
}
