package com.app.url_shortener.iam.infrastructure.adapter;

import com.app.url_shortener.iam.application.port.output.IssueAccessTokenPort;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.security.jwt.JwtAccessTokenSubject;
import com.app.url_shortener.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueAccessTokenAdapter implements IssueAccessTokenPort {

  private final JwtTokenService jwtTokenService;

  @Override
  public String getToken(AuthenticatedUserResult user) {
    JwtAccessTokenSubject tokenProperties = new JwtAccessTokenSubject(user.id(), user.plan(), user.authorities());
    return jwtTokenService.generateAccessToken(tokenProperties);
  }

  @Override
  public long getExpiresInMinutes() {
    return jwtTokenService.getExpiresInSeconds();
  }
}
