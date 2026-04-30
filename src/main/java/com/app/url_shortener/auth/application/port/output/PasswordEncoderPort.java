package com.app.url_shortener.auth.application.port.output;

public interface PasswordEncoderPort {

  String encode(String rawPassword);
}
