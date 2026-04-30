package com.app.url_shortener.iam.application.port.output;

import com.app.url_shortener.iam.domain.model.UserAccount;

import java.util.Optional;


public interface UserAccountRepositoryPort {

  UserAccount saveNewUserAccount(UserAccount userAccount);

  UserAccount save(UserAccount userAccount);

  Optional<UserAccount> findByEmail(String email);
}
