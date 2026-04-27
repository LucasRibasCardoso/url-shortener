package com.app.url_shortener.url.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Builder
@DynamoDbImmutable(builder = UrlEntity.UrlEntityBuilder.class)
public class UrlEntity {

  private final String shortCode;
  @Getter
  private final String originalUrl;
  @Getter
  private final String createdAt;

  @DynamoDbPartitionKey
  public String getShortCode() {
    return shortCode;
  }

}
