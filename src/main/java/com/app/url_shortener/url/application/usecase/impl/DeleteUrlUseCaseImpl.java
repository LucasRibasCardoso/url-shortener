package com.app.url_shortener.url.application.usecase.impl;

import com.app.url_shortener.url.application.command.DeleteUrlCommand;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.application.usecase.DeleteUrlUseCase;
import com.app.url_shortener.url.domain.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUrlUseCaseImpl implements DeleteUrlUseCase {

  private final UrlRepositoryPort urlRepositoryPort;

  @Override
  public void execute(DeleteUrlCommand command) {
    urlRepositoryPort.findByShortCode(command.shortCode())
            .filter(url -> command.canDeleteAny() || url.getUserId().equals(command.requesterId()))
            .ifPresentOrElse(
                    url -> urlRepositoryPort.delete(url.getShortCode()),
                    () -> {
                      throw new UrlNotFoundException();
                    });
  }
}
