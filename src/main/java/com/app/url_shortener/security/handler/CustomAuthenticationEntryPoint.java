package com.app.url_shortener.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.net.URI;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private static final String TYPE_UNAUTHORIZED = "/errors/unauthorized";

  @Override
  public void commence(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException authException
  ) throws IOException {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Autenticação necessária ou token inválido."
    );

    problemDetail.setTitle("Não autorizado");
    problemDetail.setType(URI.create(TYPE_UNAUTHORIZED));
    problemDetail.setProperty("errorCode", "AUTH_UNAUTHORIZED");

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

    new ObjectMapper().writeValue(response.getOutputStream(), problemDetail);
  }
}
