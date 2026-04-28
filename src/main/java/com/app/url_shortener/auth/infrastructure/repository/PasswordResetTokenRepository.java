package com.app.url_shortener.auth.infrastructure.repository;

import com.app.url_shortener.auth.infrastructure.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
}
