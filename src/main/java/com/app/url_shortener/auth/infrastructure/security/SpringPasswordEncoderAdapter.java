package com.app.url_shortener.auth.infrastructure.security;

import com.app.url_shortener.auth.application.port.output.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringPasswordEncoderAdapter implements PasswordEncoderPort {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }
}
