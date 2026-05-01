package com.app.url_shortener.security.jwt;

import java.util.List;
import java.util.UUID;

public record JwtAccessTokenSubject(UUID id, String plan, List<String> authorities) {
  public JwtAccessTokenSubject {
    authorities = List.copyOf(authorities);
  }
}
