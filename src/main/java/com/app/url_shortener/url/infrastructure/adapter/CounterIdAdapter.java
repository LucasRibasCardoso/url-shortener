package com.app.url_shortener.url.infrastructure.adapter;

import com.app.url_shortener.url.application.port.output.CounterIdRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CounterIdAdapter implements CounterIdRepository {

  private static final String COUNTER_KEY = "url:counter:id";

  private final StringRedisTemplate redisTemplate;

  public CounterIdAdapter(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Long allocateBlock(long blockSize) {
    Long blockEnd = redisTemplate.opsForValue().increment(COUNTER_KEY, blockSize);
    if (blockEnd == null) {
      throw new IllegalStateException("Nao foi possivel alocar bloco de IDs no Redis.");
    }

    return blockEnd - blockSize + 1;
  }
}
