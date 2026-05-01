package com.app.url_shortener.security.jwt;

import com.app.url_shortener.security.config.JwtProperties;
import com.app.url_shortener.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;

  public String generateAccessToken(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(
            jwtProperties.accessTokenExpirationMinutes(),
            ChronoUnit.MINUTES
    );

    List<String> authorities = principal.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.issuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(principal.getId().toString())
            .claim("plan", principal.getPlan().name())
            .claim("authorities", authorities)
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)
    ).getTokenValue();
  }

  public long getAccessTokenExpiresInSeconds() {
    return jwtProperties.accessTokenExpirationMinutes() * 60;
  }
}
