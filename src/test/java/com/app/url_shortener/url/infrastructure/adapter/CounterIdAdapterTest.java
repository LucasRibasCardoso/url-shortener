package com.app.url_shortener.url.infrastructure.adapter;

import com.app.url_shortener.config.BaseRedisSliceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("redis-slice")
@Import(CounterIdAdapter.class)
@DisplayName("Slice Redis - Adaptador de Contador de IDs")
class CounterIdAdapterTest extends BaseRedisSliceTest {

  private static final String COUNTER_KEY = "url:counter:id";

  @Autowired
  private CounterIdAdapter adapter;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    deleteCounterKey();
  }

  @AfterEach
  void tearDown() {
    deleteCounterKey();
  }

  @Nested
  @DisplayName("Alocação de bloco")
  class AllocateBlockTests {

    @Test
    @DisplayName("Deve incrementar contador no Redis e retornar primeiro ID do bloco inicial")
    void shouldIncrementRedisCounterAndReturnFirstIdOfInitialBlock() {
      // 1. Arrange
      var blockSize = 100L;

      // 2. Act
      var result = adapter.allocateBlock(blockSize);

      // 3. Assert
      assertThat(result).isEqualTo(1L);
      assertThat(redisTemplate.opsForValue().get(COUNTER_KEY)).isEqualTo("100");
    }

    @Test
    @DisplayName("Deve retornar primeiro ID do próximo bloco após incremento acumulado")
    void shouldReturnFirstIdOfNextBlockAfterAccumulatedIncrement() {
      // 1. Arrange
      var blockSize = 100L;
      adapter.allocateBlock(blockSize);

      // 2. Act
      var result = adapter.allocateBlock(blockSize);

      // 3. Assert
      assertThat(result).isEqualTo(101L);
      assertThat(redisTemplate.opsForValue().get(COUNTER_KEY)).isEqualTo("200");
    }

    @Test
    @DisplayName("Deve respeitar valor existente no Redis ao alocar novo bloco")
    void shouldRespectExistingRedisValueWhenAllocatingNewBlock() {
      // 1. Arrange
      var blockSize = 50L;
      redisTemplate.opsForValue().set(COUNTER_KEY, "250");

      // 2. Act
      var result = adapter.allocateBlock(blockSize);

      // 3. Assert
      assertThat(result).isEqualTo(251L);
      assertThat(redisTemplate.opsForValue().get(COUNTER_KEY)).isEqualTo("300");
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando incremento retornar nulo")
    @SuppressWarnings("unchecked")
    void shouldThrowIllegalStateExceptionWhenIncrementReturnsNull() {
      // 1. Arrange
      var blockSize = 100L;
      var mockedRedisTemplate = mock(StringRedisTemplate.class);
      var valueOperations = mock(ValueOperations.class);
      var adapterWithMockedTemplate = new CounterIdAdapter(mockedRedisTemplate);
      when(mockedRedisTemplate.opsForValue()).thenReturn(valueOperations);
      when(valueOperations.increment(COUNTER_KEY, blockSize)).thenReturn(null);

      // 2. Act & 3. Assert
      assertThatThrownBy(() -> adapterWithMockedTemplate.allocateBlock(blockSize))
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("Nao foi possivel alocar bloco de IDs no Redis.");
      verify(mockedRedisTemplate).opsForValue();
      verify(valueOperations).increment(COUNTER_KEY, blockSize);
      verifyNoMoreInteractions(mockedRedisTemplate, valueOperations);
    }
  }

  private void deleteCounterKey() {
    redisTemplate.delete(COUNTER_KEY);
  }
}
