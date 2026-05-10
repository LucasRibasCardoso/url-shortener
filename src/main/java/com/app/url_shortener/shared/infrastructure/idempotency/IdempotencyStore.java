package com.app.url_shortener.shared.infrastructure.idempotency;

public interface IdempotencyStore {

  boolean saveInProgress(String key, long timeoutInMinutes);

  CachedResponse getState(String key);

  void saveCompleted(String key, CachedResponse response, long timeToLiveInHours);

  void delete(String key);
}
