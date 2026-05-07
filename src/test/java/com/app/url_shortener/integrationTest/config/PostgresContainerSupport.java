package com.app.url_shortener.integrationTest.config;

import static org.testcontainers.utility.DockerImageName.parse;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public final class PostgresContainerSupport {

  private static final DockerImageName POSTGRES_IMAGE = parse("postgres:16-alpine");
  private static final String DATABASE_NAME = "url_shortener_test";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";

  private static final GenericContainer<?> POSTGRES_CONTAINER =
      new GenericContainer<>(POSTGRES_IMAGE)
          .withEnv("POSTGRES_DB", DATABASE_NAME)
          .withEnv("POSTGRES_USER", USERNAME)
          .withEnv("POSTGRES_PASSWORD", PASSWORD)
          .withExposedPorts(5432)
          .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1));

  static {
    POSTGRES_CONTAINER.start();
  }

  private PostgresContainerSupport() {}

  public static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () ->
            "jdbc:postgresql://"
                + POSTGRES_CONTAINER.getHost()
                + ":"
                + POSTGRES_CONTAINER.getMappedPort(5432)
                + "/"
                + DATABASE_NAME);
    registry.add("spring.datasource.username", () -> USERNAME);
    registry.add("spring.datasource.password", () -> PASSWORD);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
    registry.add("spring.flyway.enabled", () -> "true");
  }
}
