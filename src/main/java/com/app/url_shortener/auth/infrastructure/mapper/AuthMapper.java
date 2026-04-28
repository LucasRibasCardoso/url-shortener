package com.app.url_shortener.auth.infrastructure.mapper;

import com.app.url_shortener.auth.presentation.dto.response.AuthResponse;
import com.app.url_shortener.user.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

  default AuthResponse toAuthResponse(
          String accessToken,
          long expiresIn,
          UserResponse user
  ) {
    return new AuthResponse(
            accessToken,
            "Bearer",
            expiresIn,
            user
    );
  }
}
