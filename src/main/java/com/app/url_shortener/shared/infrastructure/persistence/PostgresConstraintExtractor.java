package com.app.url_shortener.shared.infrastructure.persistence;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class PostgresConstraintExtractor {

  private static final String POSTGRES_UNIQUE_VIOLATION = "23505";

  public Optional<String> extractUniqueConstraintName(Throwable throwable) {
    Throwable current = throwable;

    while (current != null) {
      if (current instanceof ConstraintViolationException constraintException
              && constraintException.getConstraintName() != null
              && isUniqueViolation(constraintException.getSQLException())) {
        return Optional.of(constraintException.getConstraintName());
      }

      current = current.getCause();
    }

    return Optional.empty();
  }

  private boolean isUniqueViolation(SQLException exception) {
    return exception != null && POSTGRES_UNIQUE_VIOLATION.equals(exception.getSQLState());
  }
}