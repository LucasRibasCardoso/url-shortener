package com.app.url_shortener.auth.application.service.impl;

import com.app.url_shortener.auth.application.service.RoleService;
import com.app.url_shortener.auth.domain.exception.DefaultRoleNotFoundException;
import com.app.url_shortener.auth.infrastructure.entity.RoleEntity;
import com.app.url_shortener.auth.infrastructure.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private static final String DEFAULT_USER_ROLE = "ROLE_USER";

  private final RoleRepository roleRepository;

  @Override
  @Transactional(readOnly = true)
  public RoleEntity getDefaultUserRole() {
    return roleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow(DefaultRoleNotFoundException::new);
  }
}
