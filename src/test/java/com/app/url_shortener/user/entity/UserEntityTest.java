package com.app.url_shortener.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.app.url_shortener.user.domain.enums.PlanType;
import com.app.url_shortener.user.domain.exception.InvalidUserPlanException;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.Test;

class UserEntityTest {

  @Test
  void shouldCreatePendingUserPersistedRegistrationWithFreePlanByDefault() {
    UserEntity user = UserEntity.createPendingRegistration("User Name", "user@example.com", "password-hash");

    assertThat(user.getPlan()).isEqualTo(PlanType.FREE);
  }

  @Test
  void shouldChangePlanBetweenFreeAndPremium() {
    UserEntity user = UserEntity.createPendingRegistration("User Name", "user@example.com", "password-hash");

    user.changePlan(PlanType.PREMIUM);
    assertThat(user.getPlan()).isEqualTo(PlanType.PREMIUM);

    user.changePlan(PlanType.FREE);
    assertThat(user.getPlan()).isEqualTo(PlanType.FREE);
  }

  @Test
  void shouldRejectAnonymousPlanForPersistedUser() {
    UserEntity user = UserEntity.createPendingRegistration("User Name", "user@example.com", "password-hash");

    assertThatThrownBy(() -> user.changePlan(PlanType.ANONYMOUS))
        .isInstanceOf(InvalidUserPlanException.class);
  }

  @Test
  void shouldRejectNullPlanForPersistedUser() {
    UserEntity user = UserEntity.createPendingRegistration("User Name", "user@example.com", "password-hash");

    assertThatThrownBy(() -> user.changePlan(null)).isInstanceOf(InvalidUserPlanException.class);
  }
}
