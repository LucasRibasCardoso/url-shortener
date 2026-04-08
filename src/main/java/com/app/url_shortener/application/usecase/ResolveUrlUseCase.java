package com.app.url_shortener.application.usecase;

public interface ResolveUrlUseCase {
  String execute(String shortCode);
}
