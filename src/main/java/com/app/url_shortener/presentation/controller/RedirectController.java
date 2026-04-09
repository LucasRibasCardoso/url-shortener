package com.app.url_shortener.presentation.controller;

import com.app.url_shortener.application.usecase.ResolveUrlUseCase;
import java.net.URI;

import com.app.url_shortener.presentation.docs.RedirectControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController implements RedirectControllerDocs {

  private final ResolveUrlUseCase resolveUrlUseCase;

  public RedirectController(ResolveUrlUseCase resolveUrlUseCase) {
    this.resolveUrlUseCase = resolveUrlUseCase;
  }

  @GetMapping("/{shortCode:[a-zA-Z0-9]+}")
  public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortCode) {
    String originalUrl = resolveUrlUseCase.execute(shortCode);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
  }
}
