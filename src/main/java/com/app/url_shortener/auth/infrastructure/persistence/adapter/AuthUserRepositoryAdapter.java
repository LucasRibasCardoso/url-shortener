package com.app.url_shortener.auth.infrastructure.persistence.adapter;

import com.app.url_shortener.auth.application.port.output.AuthUserRepositoryPort;
import com.app.url_shortener.auth.domain.exception.EmailAlreadyRegisteredException;
import com.app.url_shortener.auth.domain.exception.InvalidVerificationCodeException;
import com.app.url_shortener.auth.domain.model.AuthUser;
import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity;
import com.app.url_shortener.auth.infrastructure.persistence.mapper.AuthUserPersistenceMapper;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import com.app.url_shortener.user.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuthUserRepositoryAdapter implements AuthUserRepositoryPort {

  private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";
  private static final String UNIQUE_EMAIL_CONSTRAINT = "uk_users_email";

  private final UserRepository userRepository;
  private final EntityManager entityManager;
  private final AuthUserPersistenceMapper authUserPersistenceMapper;

  @Override
  public AuthUser save(AuthUser authUser) {
    Set<RoleEntity> roleEntities = authUser.getRoles().stream()
            .map(this::getRoleReference)
            .collect(Collectors.toSet());
    UserEntity entity = authUserPersistenceMapper.toPendingRegistrationEntity(authUser);
    roleEntities.forEach(entity::addRole);

    try {
      return authUserPersistenceMapper.toDomain(userRepository.saveAndFlush(entity));
    } catch (DataIntegrityViolationException exception) {
      if (isUniqueEmailViolation(exception)) {
        throw new EmailAlreadyRegisteredException();
      }
      throw exception;
    }
  }

  @Override
  public void activateEmailVerification(String email, Role role) {
    UserEntity userEntity = userRepository.findByEmailWithRoles(email).orElseThrow(InvalidVerificationCodeException::new);
    RoleEntity roleEntity = getRoleReference(role);
    userEntity.verifyEmail(roleEntity);
    userRepository.save(userEntity);
  }

  private RoleEntity getRoleReference(Role role) {
    return entityManager.getReference(RoleEntity.class, role.getId());
  }

  private boolean isUniqueEmailViolation(DataIntegrityViolationException exception) {
    Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);

    boolean isUniqueViolation = rootCause instanceof SQLException sqlException
            && UNIQUE_VIOLATION_SQL_STATE.equals(sqlException.getSQLState());
    String message = rootCause.getMessage();

    boolean isEmailConstraint = message != null
            && message.toLowerCase(Locale.ROOT).contains(UNIQUE_EMAIL_CONSTRAINT);

    return isUniqueViolation && isEmailConstraint;
  }
}
