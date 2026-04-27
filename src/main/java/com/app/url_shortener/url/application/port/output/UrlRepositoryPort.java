package com.app.url_shortener.url.application.port.output;

import com.app.url_shortener.url.domain.model.Url;
import java.util.Optional;

public interface UrlRepositoryPort {

  void save(Url url);

  Optional<Url> findByShortCode(String shortCode);
}
