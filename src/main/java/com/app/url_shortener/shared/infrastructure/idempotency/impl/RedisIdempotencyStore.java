package com.app.url_shortener.shared.infrastructure.idempotency.impl;

import com.app.url_shortener.shared.exception.internalservererror.IdempotencyCacheException;
import com.app.url_shortener.shared.infrastructure.idempotency.CachedResponse;
import com.app.url_shortener.shared.infrastructure.idempotency.IdempotencyStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisIdempotencyStore implements IdempotencyStore {

  private static final String KEY_PREFIX = "idempotency:";
  private static final String IN_PROGRESS_STATE = "IN_PROGRESS";

  private final ObjectMapper objectMapper;
  private final StringRedisTemplate redisTemplate;

  @Override
  public boolean saveInProgress(String key, long timeoutInMinutes) {
    return Boolean.TRUE.equals(
            redisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + key, IN_PROGRESS_STATE, Duration.ofMinutes(timeoutInMinutes))
    );
  }

  @Override
  public CachedResponse getState(String key) {
    String state = redisTemplate.opsForValue().get(KEY_PREFIX + key);

    if (state == null || state.isBlank() || IN_PROGRESS_STATE.equals(state)) {
      return null;
    }

    try {
      return objectMapper.readValue(state, CachedResponse.class);
    } catch (Exception e) {
      throw new IdempotencyCacheException("Falha ao desserializar o cache de idempotency-key");
    }

  }

  @Override
  public void saveCompleted(String key, CachedResponse cachedResponse, long timeToLiveInHours) {
    try {
      String responseJson = objectMapper.writeValueAsString(cachedResponse);
      redisTemplate.opsForValue().set(KEY_PREFIX + key, responseJson, Duration.ofHours(timeToLiveInHours));
    } catch (Exception e) {
      throw new IdempotencyCacheException("Erro ao serializar o cache de idempotência para o Redis");
    }
  }

  @Override
  public void delete(String key) {
    redisTemplate.delete(KEY_PREFIX + key);
  }
}
