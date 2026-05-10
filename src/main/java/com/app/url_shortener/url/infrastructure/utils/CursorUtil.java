package com.app.url_shortener.url.infrastructure.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class CursorUtil {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static String encode(Map<String, AttributeValue> lastKey) {
    try {
      return Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(toSerializableMap(lastKey)));
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
      Map<String, Map<String, String>> value =
          mapper.readValue(decoded, new TypeReference<>() {});
      return toAttributeValueMap(value);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao decodificar cursor", e);
    }
  }

  private static Map<String, Map<String, String>> toSerializableMap(Map<String, AttributeValue> lastKey) {
    if (lastKey == null) {
      return null;
    }

    Map<String, Map<String, String>> result = new LinkedHashMap<>();
    lastKey.forEach((key, value) -> result.put(key, toSerializableValue(value)));
    return result;
  }

  private static Map<String, String> toSerializableValue(AttributeValue value) {
    if (value.s() != null) {
      return Map.of("S", value.s());
    }
    if (value.n() != null) {
      return Map.of("N", value.n());
    }
    if (value.b() != null) {
      return Map.of("B", Base64.getEncoder().encodeToString(value.b().asByteArray()));
    }

    throw new IllegalArgumentException("Unsupported cursor attribute value type.");
  }

  private static Map<String, AttributeValue> toAttributeValueMap(Map<String, Map<String, String>> value) {
    if (value == null) {
      return null;
    }

    Map<String, AttributeValue> result = new LinkedHashMap<>();
    value.forEach((key, attribute) -> result.put(key, toAttributeValue(attribute)));
    return result;
  }

  private static AttributeValue toAttributeValue(Map<String, String> value) {
    if (value.containsKey("S")) {
      return AttributeValue.builder().s(value.get("S")).build();
    }
    if (value.containsKey("N")) {
      return AttributeValue.builder().n(value.get("N")).build();
    }
    if (value.containsKey("B")) {
      return AttributeValue.builder()
          .b(SdkBytes.fromByteArray(Base64.getDecoder().decode(value.get("B"))))
          .build();
    }

    throw new IllegalArgumentException("Unsupported cursor attribute value type.");
  }
}
