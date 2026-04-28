package com.app.url_shortener.shared.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.url_shortener.auth.infrastructure.entity.PasswordResetTokenEntity;
import com.app.url_shortener.auth.infrastructure.entity.PermissionEntity;
import com.app.url_shortener.auth.infrastructure.entity.RefreshTokenEntity;
import com.app.url_shortener.auth.infrastructure.entity.RoleEntity;
import com.app.url_shortener.auth.infrastructure.repository.PasswordResetTokenRepository;
import com.app.url_shortener.auth.infrastructure.repository.PermissionRepository;
import com.app.url_shortener.auth.infrastructure.repository.RefreshTokenRepository;
import com.app.url_shortener.auth.infrastructure.repository.RoleRepository;
import com.app.url_shortener.shared.config.JpaAuditingConfig;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import com.app.url_shortener.user.domain.enums.PlanType;
import com.app.url_shortener.user.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class JpaAuditingDataJpaTest {

  private static final AtomicInteger SEQUENCE = new AtomicInteger();

  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private PermissionRepository permissionRepository;
  @Autowired private RefreshTokenRepository refreshTokenRepository;
  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void shouldAuditUserEntityAndKeepCreatedAtImmutableOnUpdate() throws Exception {
    UserEntity user =
        UserEntity.create(
            "Audit User " + uniqueValue(),
            "audit-user-" + uniqueValue() + "@example.com",
            "password-hash",
            null);

    assertThat(user.getId()).isNotNull();
    assertThat(user.getPlan()).isEqualTo(PlanType.FREE);
    assertThat(user.getCreatedAt()).isNull();
    assertThat(user.getUpdatedAt()).isNull();

    UserEntity savedUser = userRepository.saveAndFlush(user);

    Instant createdAt = savedUser.getCreatedAt();
    Instant updatedAt = savedUser.getUpdatedAt();

    assertThat(createdAt).isNotNull();
    assertThat(updatedAt).isNotNull();

    entityManager.clear();
    waitForNextAuditTick();

    UserEntity persistedUser = userRepository.findById(user.getId()).orElseThrow();
    persistedUser.changeName("Updated User " + uniqueValue());
    userRepository.saveAndFlush(persistedUser);

    entityManager.clear();

    UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getCreatedAt()).isEqualTo(createdAt);
    assertThat(updatedUser.getUpdatedAt()).isAfter(updatedAt);
  }

  @Test
  void shouldAuditRoleEntityAndKeepCreatedAtImmutableOnUpdate() throws Exception {
    RoleEntity role = RoleEntity.create("role-audit-" + uniqueValue());

    assertThat(role.getId()).isNotNull();
    assertThat(role.getCreatedAt()).isNull();
    assertThat(role.getUpdatedAt()).isNull();

    RoleEntity savedRole = roleRepository.saveAndFlush(role);

    Instant createdAt = savedRole.getCreatedAt();
    Instant updatedAt = savedRole.getUpdatedAt();

    assertThat(createdAt).isNotNull();
    assertThat(updatedAt).isNotNull();

    entityManager.clear();
    waitForNextAuditTick();

    RoleEntity persistedRole = roleRepository.findById(role.getId()).orElseThrow();
    persistedRole.changeName("role-audit-updated-" + uniqueValue());
    roleRepository.saveAndFlush(persistedRole);

    entityManager.clear();

    RoleEntity updatedRole = roleRepository.findById(role.getId()).orElseThrow();
    assertThat(updatedRole.getCreatedAt()).isEqualTo(createdAt);
    assertThat(updatedRole.getUpdatedAt()).isAfter(updatedAt);
  }

  @Test
  void shouldAuditPermissionEntityAndKeepCreatedAtImmutableOnUpdate() throws Exception {
    PermissionEntity permission =
        PermissionEntity.create(
            "permission-audit-" + uniqueValue(),
            "Permission created for auditing test");

    assertThat(permission.getId()).isNotNull();
    assertThat(permission.getCreatedAt()).isNull();
    assertThat(permission.getUpdatedAt()).isNull();

    PermissionEntity savedPermission = permissionRepository.saveAndFlush(permission);

    Instant createdAt = savedPermission.getCreatedAt();
    Instant updatedAt = savedPermission.getUpdatedAt();

    assertThat(createdAt).isNotNull();
    assertThat(updatedAt).isNotNull();

    entityManager.clear();
    waitForNextAuditTick();

    PermissionEntity persistedPermission =
        permissionRepository.findById(permission.getId()).orElseThrow();
    persistedPermission.changeDescription("Permission updated for auditing test");
    permissionRepository.saveAndFlush(persistedPermission);

    entityManager.clear();

    PermissionEntity updatedPermission =
        permissionRepository.findById(permission.getId()).orElseThrow();
    assertThat(updatedPermission.getCreatedAt()).isEqualTo(createdAt);
    assertThat(updatedPermission.getUpdatedAt()).isAfter(updatedAt);
  }

  @Test
  void shouldGenerateUuidAndCreatedDateForRefreshTokenEntity() {
    UserEntity user = persistUser("refresh-token-owner");
    RefreshTokenEntity refreshToken =
        RefreshTokenEntity.issue(
            user,
            "refresh-token-hash-" + uniqueValue(),
            Instant.now().plusSeconds(3600),
            "JUnit",
            null,
            "Test Device");

    assertThat(refreshToken.getId()).isNotNull();
    assertThat(refreshToken.getCreatedAt()).isNull();

    refreshTokenRepository.saveAndFlush(refreshToken);

    entityManager.clear();

    RefreshTokenEntity persistedToken =
        refreshTokenRepository.findById(refreshToken.getId()).orElseThrow();
    assertThat(persistedToken.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldGenerateUuidAndCreatedDateForPasswordResetTokenEntity() {
    UserEntity user = persistUser("password-reset-owner");
    PasswordResetTokenEntity resetToken =
        PasswordResetTokenEntity.issue(
            user, "password-reset-hash-" + uniqueValue(), Instant.now().plusSeconds(3600));

    assertThat(resetToken.getId()).isNotNull();
    assertThat(resetToken.getCreatedAt()).isNull();

    passwordResetTokenRepository.saveAndFlush(resetToken);

    entityManager.clear();

    PasswordResetTokenEntity persistedToken =
        passwordResetTokenRepository.findById(resetToken.getId()).orElseThrow();
    assertThat(persistedToken.getCreatedAt()).isNotNull();
  }

  private UserEntity persistUser(String prefix) {
    UserEntity user =
        UserEntity.create(
            prefix + "-" + uniqueValue(),
            prefix + "-" + uniqueValue() + "@example.com",
            "password-hash",
            null);

    return userRepository.saveAndFlush(user);
  }

  private static String uniqueValue() {
    return Integer.toString(SEQUENCE.incrementAndGet());
  }

  private static void waitForNextAuditTick() throws InterruptedException {
    Thread.sleep(20);
  }
}
