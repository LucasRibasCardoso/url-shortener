package com.app.url_shortener.iam.application.port.output;

public interface PasswordEncoderPort {

  String encode(String rawPassword);
}
