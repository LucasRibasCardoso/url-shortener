package com.app.url_shortener.auth.application.service.impl;

import com.app.url_shortener.auth.application.service.AuthService;
import com.app.url_shortener.auth.application.service.RoleService;
import com.app.url_shortener.auth.infrastructure.entity.RoleEntity;
import com.app.url_shortener.auth.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.auth.presentation.dto.response.AuthResponse;
import com.app.url_shortener.security.jwt.JwtTokenService;
import com.app.url_shortener.security.principal.UserPrincipal;
import com.app.url_shortener.security.principal.UserPrincipalFactory;
import com.app.url_shortener.user.domain.exception.EmailAlreadyExistsException;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import com.app.url_shortener.user.infrastructure.mapper.UserMapper;
import com.app.url_shortener.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RoleService roleService;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final UserPrincipalFactory userPrincipalFactory;
  private final JwtTokenService jwtTokenService;

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest registerRequest) {
    String normalizedEmail = normalizeEmail(registerRequest.email());
    String normalizedName = normalizeName(registerRequest.name());
    String passwordHash = passwordEncoder.encode(registerRequest.password());

    RoleEntity defaultRole = roleService.getDefaultUserRole();

    UserEntity userEntity = UserEntity.create(
            normalizedName,
            normalizedEmail,
            passwordHash,
            Set.of(defaultRole));

    try {
      UserEntity createdUser = userRepository.saveAndFlush(userEntity);
      UserPrincipal userPrincipal = userPrincipalFactory.from(createdUser);
      String accessToken = jwtTokenService.generateAccessToken(userPrincipal);
      return new AuthResponse(
              accessToken,
              "Bearer",
              jwtTokenService.getAccessTokenExpiresInSeconds(),
              userMapper.toResponse(createdUser)
      );

    } catch (DataIntegrityViolationException e) {
      if (isUniqueEmailViolation(e)) {
        throw new EmailAlreadyExistsException();
      }
      throw e;
    }
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeName(String name) {
    return name.trim();
  }

  private boolean isUniqueEmailViolation(DataIntegrityViolationException exception) {
    Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);
    return rootCause.getMessage() != null && rootCause.getMessage().contains("uk_users_email");
  }
}
