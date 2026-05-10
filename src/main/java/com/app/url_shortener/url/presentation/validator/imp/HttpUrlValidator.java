package com.app.url_shortener.url.presentation.validator.imp;

import com.app.url_shortener.url.presentation.validator.ValidHttpUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;

public class HttpUrlValidator implements ConstraintValidator<ValidHttpUrl, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true; // NotBlank trata sozinho
    try {
      URI uri = URI.create(value.trim());
      String scheme = uri.getScheme();
      return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
          && uri.getHost() != null
          && !uri.getHost().isBlank();
    } catch (Exception ex) {
      return false;
    }
  }
}
