package com.app.url_shortener.auth.application.port.output;

import com.app.url_shortener.auth.domain.model.AuthUser;
import com.app.url_shortener.auth.domain.model.Role;


public interface AuthUserRepositoryPort {

  AuthUser save(AuthUser authUser);

  void activateEmailVerification(String email, Role role);
}
