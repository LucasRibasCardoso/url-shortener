package com.app.url_shortener.application.usecase.imp;

import com.app.url_shortener.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.application.usecase.ResolveUrlUseCase;
import com.app.url_shortener.domain.exception.notFound.UrlNotFoundException;
import com.app.url_shortener.domain.model.Url;
import org.springframework.stereotype.Service;

@Service
public class ResolveUrlUseCaseImp implements ResolveUrlUseCase {

  private final UrlRepositoryPort urlRepositoryPort;

  public ResolveUrlUseCaseImp(UrlRepositoryPort urlRepositoryPort) {
    this.urlRepositoryPort = urlRepositoryPort;
  }

  @Override
  public String execute(String shortCode) {
    return urlRepositoryPort
        .findByShortCode(shortCode)
        .map(Url::getOriginalUrl)
        .orElseThrow(UrlNotFoundException::new);
  }
}
