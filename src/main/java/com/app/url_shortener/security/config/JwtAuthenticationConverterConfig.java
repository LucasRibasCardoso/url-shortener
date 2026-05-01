package com.app.url_shortener.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;

@Configuration
public class JwtAuthenticationConverterConfig {

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      List<String> authorities = jwt.getClaimAsStringList("authorities");

      if (authorities == null) {
        return List.of();
      }

      return authorities.stream()
              .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
              .toList();
    });

    converter.setPrincipalClaimName("sub");

    return converter;
  }
}
