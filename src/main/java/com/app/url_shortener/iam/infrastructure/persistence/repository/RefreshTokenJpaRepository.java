package com.app.url_shortener.iam.infrastructure.persistence.repository;

import com.app.url_shortener.iam.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

  Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("""
              update RefreshTokenEntity r
                 set r.revokedAt = :revokedAt
               where r.user.id = :userId
                 and r.revokedAt is null
          """)
  void revokeAllActiveTokensByUserId(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("""
              update RefreshTokenEntity rt
                 set rt.revokedAt = :revokedAt
               where rt.tokenHash = :tokenHash
                 and rt.revokedAt is null
          """)
  void revokeActiveTokenByHash(@Param("tokenHash") String tokenHash, @Param("revokedAt") Instant revokedAt);


  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt < :cutoffDate")
  int deleteExpiredTokensBefore(@Param("cutoffDate") Instant cutoffDate);

}
