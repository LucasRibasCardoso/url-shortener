package com.app.url_shortener.url.infrastructure.utils;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class CursorUtil {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static String encode(Map<String, AttributeValue> lastKey) {
    try {
      return Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(lastKey));
    } catch (Exception e) {
      throw new RuntimeException("Erro ao codificar cursor", e);
    }
  }

  public static Map<String, AttributeValue> decode(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }
    try {
      byte[] decoded = Base64.getDecoder().decode(cursor);
      return mapper.readValue(decoded, new TypeReference<Map<String, AttributeValue>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Erro ao decodificar cursor", e);
    }
  }
}