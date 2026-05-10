package com.app.url_shortener.config;

import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataRedisTest
public abstract class BaseRedisSliceTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    RedisContainerSupport.registerRedisProperties(registry);
  }

}
