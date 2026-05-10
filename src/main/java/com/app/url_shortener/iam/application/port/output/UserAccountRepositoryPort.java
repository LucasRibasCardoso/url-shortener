package com.app.url_shortener.iam.application.port.output;

import com.app.url_shortener.iam.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;


public interface UserAccountRepositoryPort {

  UserAccount saveNewUserAccount(UserAccount userAccount);

  UserAccount save(UserAccount userAccount);

  Optional<UserAccount> findByEmail(String email);

  Optional<UserAccount> findById(UUID id);
}
