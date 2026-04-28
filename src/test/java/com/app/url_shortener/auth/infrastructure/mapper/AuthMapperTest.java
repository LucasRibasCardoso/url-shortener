package com.app.url_shortener.auth.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.url_shortener.user.presentation.dto.response.UserResponse;
import com.app.url_shortener.auth.infrastructure.entity.RoleEntity;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import com.app.url_shortener.user.domain.enums.PlanType;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AuthMapperTest {

  private final AuthMapper authMapper = new AuthMapper() {};

  @Test
  void shouldMapUserPlanToUserResponse() {
    UserEntity user =
        UserEntity.create("Mapper User", "mapper@example.com", "password-hash", Set.of(RoleEntity.create("ADMIN")));
    user.changePlan(PlanType.PREMIUM);

    UserResponse response = authMapper.toUserResponse(user);

    assertThat(response).isNotNull();
    assertThat(response.plan()).isEqualTo(PlanType.PREMIUM);
    assertThat(response.roles()).containsExactly("ADMIN");
  }
}
