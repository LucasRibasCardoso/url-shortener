package com.app.url_shortener.auth.infrastructure.repository;

import com.app.url_shortener.auth.infrastructure.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
}
