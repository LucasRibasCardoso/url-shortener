package com.app.url_shortener.application.usecase.imp;

import com.app.url_shortener.application.port.output.UrlEncoderPort;
import com.app.url_shortener.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.application.service.IdGeneratorService;
import com.app.url_shortener.application.usecase.ShortenUrlUseCase;
import com.app.url_shortener.domain.model.Url;
import org.springframework.stereotype.Service;

@Service
public class ShortenUrlUseCaseImp implements ShortenUrlUseCase {

  private final IdGeneratorService idGeneratorService;
  private final UrlRepositoryPort urlRepositoryPort;
  private final UrlEncoderPort urlEncoderPort;

  public ShortenUrlUseCaseImp(
      IdGeneratorService idGeneratorService,
      UrlRepositoryPort urlRepositoryPort,
      UrlEncoderPort urlEncoderPort) {
    this.idGeneratorService = idGeneratorService;
    this.urlRepositoryPort = urlRepositoryPort;
    this.urlEncoderPort = urlEncoderPort;
  }

  @Override
  public Url execute(String originalUrl) {
    long uniqueId = idGeneratorService.generateId();
    String shortCode = urlEncoderPort.encode(uniqueId);

    Url url = Url.create(shortCode, originalUrl);
    urlRepositoryPort.save(url);

    return url;
  }
}
