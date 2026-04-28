package com.app.url_shortener.user.infrastructure.mapper;

import com.app.url_shortener.security.principal.UserPrincipal;
import com.app.url_shortener.user.presentation.dto.response.UserResponse;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toResponse(UserEntity entity);

  UserResponse toResponse(UserPrincipal principal);
}
