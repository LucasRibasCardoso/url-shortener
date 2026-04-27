package com.app.url_shortener.url.application.usecase;

public interface ResolveUrlUseCase {
  String execute(String shortCode);
}
