package com.app.url_shortener.unitTest.iam.infrastructure.adapter;

import com.app.url_shortener.iam.domain.enums.PlanType;
import com.app.url_shortener.iam.domain.enums.UserStatus;
import com.app.url_shortener.iam.domain.model.UserAccount;
import com.app.url_shortener.iam.infrastructure.adapter.UserAccountRepositoryAdapter;
import com.app.url_shortener.iam.infrastructure.persistence.entity.UserEntity;
import com.app.url_shortener.iam.infrastructure.persistence.mapper.UserAccountPersistenceMapper;
import com.app.url_shortener.iam.infrastructure.persistence.repository.UserJpaRepository;
import com.app.url_shortener.shared.infrastructure.persistence.DataIntegrityExceptionTranslator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Adaptador de Repositório de Contas de Usuário")
class UserAccountRepositoryAdapterTest {

  @Mock
  private UserJpaRepository userJpaRepository;

  @Mock
  private DataIntegrityExceptionTranslator dataIntegrityExceptionTranslator;

  @Mock
  private UserAccountPersistenceMapper userAccountPersistenceMapper;

  @InjectMocks
  private UserAccountRepositoryAdapter adapter;

  @Nested
  @DisplayName("Salvamento")
  class SaveTests {

    @Test
    @DisplayName("Deve mapear domínio, salvar entidade e retornar domínio salvo")
    void shouldMapDomainSaveEntityAndReturnSavedDomain() {
      // 1. Arrange
      var userAccount = userAccount();
      var userEntity = userEntity();
      var savedEntity = userEntity();
      var savedUserAccount = activeUserAccount();

      given(userAccountPersistenceMapper.toEntity(userAccount)).willReturn(userEntity);
      given(userJpaRepository.saveAndFlush(userEntity)).willReturn(savedEntity);
      given(userAccountPersistenceMapper.toDomain(savedEntity)).willReturn(savedUserAccount);

      // 2. Act
      var result = adapter.save(userAccount);

      // 3. Assert
      assertThat(result).isEqualTo(savedUserAccount);

      verify(userAccountPersistenceMapper).toEntity(userAccount);
      verify(userJpaRepository).saveAndFlush(userEntity);
      verify(userAccountPersistenceMapper).toDomain(savedEntity);
      verifyNoInteractions(dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository, userAccountPersistenceMapper);
    }
  }

  @Nested
  @DisplayName("Salvamento de Nova Conta")
  class SaveNewUserAccountTests {

    @Test
    @DisplayName("Deve mapear domínio, salvar nova entidade e retornar domínio salvo")
    void shouldMapDomainSaveNewEntityAndReturnSavedDomain() {
      // 1. Arrange
      var userAccount = userAccount();
      var userEntity = userEntity();
      var savedEntity = userEntity();
      var savedUserAccount = activeUserAccount();

      given(userAccountPersistenceMapper.toEntity(userAccount)).willReturn(userEntity);
      given(userJpaRepository.saveAndFlush(userEntity)).willReturn(savedEntity);
      given(userAccountPersistenceMapper.toDomain(savedEntity)).willReturn(savedUserAccount);

      // 2. Act
      var result = adapter.saveNewUserAccount(userAccount);

      // 3. Assert
      assertThat(result).isEqualTo(savedUserAccount);

      verify(userAccountPersistenceMapper).toEntity(userAccount);
      verify(userJpaRepository).saveAndFlush(userEntity);
      verify(userAccountPersistenceMapper).toDomain(savedEntity);
      verifyNoInteractions(dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository, userAccountPersistenceMapper);
    }

    @Test
    @DisplayName("Deve traduzir exceção de integridade ao salvar nova conta")
    void shouldTranslateDataIntegrityExceptionWhenSavingNewAccount() {
      // 1. Arrange
      var userAccount = userAccount();
      var userEntity = userEntity();
      var dataIntegrityException = new DataIntegrityViolationException("unique constraint violation");
      var translatedException = new IllegalStateException("Email já cadastrado.");

      given(userAccountPersistenceMapper.toEntity(userAccount)).willReturn(userEntity);
      given(userJpaRepository.saveAndFlush(userEntity)).willThrow(dataIntegrityException);
      given(dataIntegrityExceptionTranslator.translate(dataIntegrityException)).willReturn(translatedException);

      // 2. Act
      var throwableAssert = assertThatThrownBy(() -> adapter.saveNewUserAccount(userAccount));

      // 3. Assert
      throwableAssert
              .isInstanceOf(IllegalStateException.class)
              .hasMessage("Email já cadastrado.");

      verify(userAccountPersistenceMapper).toEntity(userAccount);
      verify(userJpaRepository).saveAndFlush(userEntity);
      verify(dataIntegrityExceptionTranslator).translate(dataIntegrityException);
      verifyNoMoreInteractions(userJpaRepository, userAccountPersistenceMapper, dataIntegrityExceptionTranslator);
    }
  }

  @Nested
  @DisplayName("Busca por Email")
  class FindByEmailTests {

    @Test
    @DisplayName("Deve retornar usuário quando entidade existir para o email informado")
    void shouldReturnUserWhenEntityExistsForEmail() {
      // 1. Arrange
      var email = "user@email.com";
      var entity = userEntity();
      var domain = activeUserAccount();

      given(userJpaRepository.findByEmailWithRolesAndPermissions(email)).willReturn(Optional.of(entity));
      given(userAccountPersistenceMapper.toDomain(entity)).willReturn(domain);

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).contains(domain);

      verify(userJpaRepository).findByEmailWithRolesAndPermissions(email);
      verify(userAccountPersistenceMapper).toDomain(entity);
      verifyNoInteractions(dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository, userAccountPersistenceMapper);
    }

    @Test
    @DisplayName("Deve retornar vazio quando nenhuma entidade existir para o email informado")
    void shouldReturnEmptyWhenNoEntityExistsForEmail() {
      // 1. Arrange
      var email = "missing@email.com";

      given(userJpaRepository.findByEmailWithRolesAndPermissions(email)).willReturn(Optional.empty());

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).isEmpty();

      verify(userJpaRepository).findByEmailWithRolesAndPermissions(email);
      verifyNoInteractions(userAccountPersistenceMapper, dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository);
    }
  }

  @Nested
  @DisplayName("Busca por ID")
  class FindByIdTests {

    @Test
    @DisplayName("Deve retornar usuário quando entidade existir para o ID informado")
    void shouldReturnUserWhenEntityExistsForId() {
      // 1. Arrange
      var id = UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123");
      var entity = userEntity();
      var domain = activeUserAccount();

      given(userJpaRepository.findById(id)).willReturn(Optional.of(entity));
      given(userAccountPersistenceMapper.toDomain(entity)).willReturn(domain);

      // 2. Act
      var result = adapter.findById(id);

      // 3. Assert
      assertThat(result).contains(domain);

      verify(userJpaRepository).findById(id);
      verify(userAccountPersistenceMapper).toDomain(entity);
      verifyNoInteractions(dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository, userAccountPersistenceMapper);
    }

    @Test
    @DisplayName("Deve retornar vazio quando nenhuma entidade existir para o ID informado")
    void shouldReturnEmptyWhenNoEntityExistsForId() {
      // 1. Arrange
      var id = UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac999");

      given(userJpaRepository.findById(id)).willReturn(Optional.empty());

      // 2. Act
      var result = adapter.findById(id);

      // 3. Assert
      assertThat(result).isEmpty();

      verify(userJpaRepository).findById(id);
      verifyNoInteractions(userAccountPersistenceMapper, dataIntegrityExceptionTranslator);
      verifyNoMoreInteractions(userJpaRepository);
    }
  }

  private UserAccount userAccount() {
    return UserAccount.restore(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            "User Name",
            "user@email.com",
            "password-hash",
            UserStatus.PENDING_EMAIL_VERIFICATION,
            PlanType.FREE,
            false,
            Set.of()
    );
  }

  private UserAccount activeUserAccount() {
    return UserAccount.restore(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            "User Name",
            "user@email.com",
            "password-hash",
            UserStatus.ACTIVE,
            PlanType.FREE,
            true,
            Set.of()
    );
  }

  private UserEntity userEntity() {
    return new UserEntity(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            "User Name",
            "user@email.com",
            "password-hash",
            UserStatus.PENDING_EMAIL_VERIFICATION,
            PlanType.FREE,
            false,
            null,
            null,
            Set.of()
    );
  }
}
