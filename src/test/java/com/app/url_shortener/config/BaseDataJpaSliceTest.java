package com.app.url_shortener.config;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseDataJpaSliceTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    PostgresContainerSupport.registerDatasourceProperties(registry);
  }
}
