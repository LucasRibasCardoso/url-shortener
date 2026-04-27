package com.app.url_shortener.url.infrastructure.adapter.hashids;

import com.app.url_shortener.url.application.port.output.UrlEncoderPort;
import jakarta.annotation.PostConstruct;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashidsEncoderAdapter implements UrlEncoderPort {

  private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private final String salt;
  private final int minLength;

  private Hashids hashids;

  public HashidsEncoderAdapter(
      @Value("${app.hashids.salt}") String salt,
      @Value("${app.hashids.min-length}") int minLength
  ) {
    this.salt = salt;
    this.minLength = minLength;
  }

  @PostConstruct
  public void initialize() {
    this.hashids = new Hashids(salt, minLength, BASE62_ALPHABET);
  }

  @Override
  public String encode(Long id) {
    return hashids.encode(id);
  }
}

