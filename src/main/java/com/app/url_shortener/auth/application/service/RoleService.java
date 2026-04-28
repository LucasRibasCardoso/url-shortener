package com.app.url_shortener.auth.application.service;

import com.app.url_shortener.auth.infrastructure.entity.RoleEntity;

public interface RoleService {
  RoleEntity getDefaultUserRole();
}
