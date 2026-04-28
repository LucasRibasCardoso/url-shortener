package com.app.url_shortener.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private static final String TYPE_FORBIDDEN = "/errors/forbidden";

  @Override
  public void handle(
          HttpServletRequest request,
          HttpServletResponse response,
          AccessDeniedException accessDeniedException
  ) throws IOException {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            "Você não possui permissão para acessar este recurso."
    );

    problemDetail.setTitle("Acesso negado");
    problemDetail.setType(URI.create(TYPE_FORBIDDEN));
    problemDetail.setProperty("errorCode", "AUTH_ACCESS_DENIED");

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

    new ObjectMapper().writeValue(response.getOutputStream(), problemDetail);
  }
}