package com.app.url_shortener.security.principal;

import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserPrincipalFactory {

  public UserPrincipal from(UserEntity user) {
    Set<GrantedAuthority> authorities = user.getRoles()
            .stream()
            .flatMap(role -> Stream.concat(
                    Stream.of(new SimpleGrantedAuthority(role.getName())),
                    role.getPermissions()
                            .stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
            ))
            .collect(Collectors.toSet());

    return new UserPrincipal(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getPlan(),
            user.getStatus(),
            authorities
    );
  }
}
