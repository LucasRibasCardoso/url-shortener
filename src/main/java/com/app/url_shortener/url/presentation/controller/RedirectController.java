package com.app.url_shortener.url.presentation.controller;

import com.app.url_shortener.url.application.command.ResolveUrlCommand;
import com.app.url_shortener.url.application.result.ResolvedUrlResult;
import com.app.url_shortener.url.application.usecase.ResolveUrlUseCase;
import com.app.url_shortener.url.presentation.docs.RedirectControllerDocs;
import com.app.url_shortener.url.presentation.mapper.UrlWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/r")
@RestController
@RequiredArgsConstructor
public class RedirectController implements RedirectControllerDocs {

  private final UrlWebMapper urlWebMapper;
  private final ResolveUrlUseCase resolveUrlUseCase;

  @GetMapping("/{shortCode:[a-zA-Z0-9]+}")
  public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortCode) {
    ResolveUrlCommand command = urlWebMapper.toCommand(shortCode);
    ResolvedUrlResult result = resolveUrlUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(result.originalUrl())).build();
  }
}
