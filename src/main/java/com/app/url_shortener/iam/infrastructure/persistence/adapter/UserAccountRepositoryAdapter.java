package com.app.url_shortener.iam.infrastructure.persistence.adapter;

import com.app.url_shortener.iam.application.port.output.UserAccountRepositoryPort;
import com.app.url_shortener.iam.domain.exception.EmailAlreadyRegisteredException;
import com.app.url_shortener.iam.domain.model.UserAccount;
import com.app.url_shortener.iam.infrastructure.persistence.entity.UserEntity;
import com.app.url_shortener.iam.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.app.url_shortener.iam.infrastructure.persistence.mapper.UserAccountPersistenceMapper;
import com.app.url_shortener.iam.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryAdapter implements UserAccountRepositoryPort {

  private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";
  private static final String UNIQUE_EMAIL_CONSTRAINT = "uk_users_email";

  private final UserJpaRepository userJpaRepository;
  private final RolePersistenceMapper rolePersistenceMapper;
  private final UserAccountPersistenceMapper userAccountPersistenceMapper;

  @Override
  public UserAccount save(UserAccount userAccount) {
    UserEntity userEntity = userAccountPersistenceMapper.toEntity(userAccount);
    UserEntity savedEntity = userJpaRepository.save(userEntity);
    return userAccountPersistenceMapper.toDomain(savedEntity);
  }

  /**
   * Usado somente para criar um novo usuário. Para atualizações, use o metodo save(UserAccount).
   *
   * @param userAccount
   * @return
   */
  @Override
  public UserAccount saveNewUserAccount(UserAccount userAccount) {
    try {
      UserEntity userEntity = userAccountPersistenceMapper.toEntity(userAccount);
      UserEntity savedEntity = userJpaRepository.save(userEntity);
      return userAccountPersistenceMapper.toDomain(savedEntity);
    } catch (DataIntegrityViolationException e) {
      if (isUniqueEmailViolation(e)) {
        throw new EmailAlreadyRegisteredException();
      }
      throw e;
    }
  }

  @Override
  public Optional<UserAccount> findByEmail(String email) {
    return userJpaRepository.findByEmailWithRolesAndPermissions(email).map(userAccountPersistenceMapper::toDomain);
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
