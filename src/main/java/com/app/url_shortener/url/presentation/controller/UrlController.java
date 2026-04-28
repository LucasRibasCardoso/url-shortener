package com.app.url_shortener.url.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {


  // TODO: Endpoint para buscar todas URLs do usuário (GET /me)
  // TODO: Endpoint para buscar detalhes de uma URL específica (GET /{shortcode})
  // TODO: Endpoint para deletar uma URL (DELETE /{shortcode})
}
