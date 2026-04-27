package com.app.url_shortener.url.infrastructure.adapter.dynamo;

import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.domain.exception.conflict.ShortCodeCollisionException;
import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.infrastructure.mapper.UrlMapper;
import com.app.url_shortener.url.infrastructure.persistence.entity.UrlEntity;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Repository
public class UrlRepositoryAdapter implements UrlRepositoryPort {

  private static final String SHORT_CODE_ATTRIBUTE = "shortCode";

  private final DynamoDbTable<UrlEntity> urlTable;
  private final UrlMapper urlMapper;

  public UrlRepositoryAdapter(DynamoDbTable<UrlEntity> urlTable, UrlMapper urlMapper) {
    this.urlTable = urlTable;
    this.urlMapper = urlMapper;
  }

  @Override
  public void save(Url url) {
    Expression condition =
        Expression.builder()
            .expression("attribute_not_exists(#pk)")
            .putExpressionName("#pk", SHORT_CODE_ATTRIBUTE)
            .build();

    UrlEntity entity = urlMapper.toEntity(url);
    PutItemEnhancedRequest<UrlEntity> request =
        PutItemEnhancedRequest.builder(UrlEntity.class)
            .item(entity)
            .conditionExpression(condition)
            .build();

    try {
      urlTable.putItem(request);
    } catch (ConditionalCheckFailedException exception) {
      throw new ShortCodeCollisionException(exception);
    }
  }

  @Override
  @Cacheable(value = "urls", key = "#shortCode", unless = "#result == null")
  public Optional<Url> findByShortCode(String shortCode) {
    Key key = Key.builder().partitionValue(shortCode).build();
    UrlEntity entity = urlTable.getItem(r -> r.key(key));
    return Optional.ofNullable(entity).map(urlMapper::toDomain);
  }
}
