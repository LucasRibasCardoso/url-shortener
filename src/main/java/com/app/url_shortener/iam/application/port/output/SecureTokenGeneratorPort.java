package com.app.url_shortener.iam.application.port.output;

public interface SecureTokenGeneratorPort {

  String generateRandomToken();

  String hashToken(String rawToken);
}
