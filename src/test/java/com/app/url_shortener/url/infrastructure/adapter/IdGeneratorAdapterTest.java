package com.app.url_shortener.url.infrastructure.adapter;

import com.app.url_shortener.url.application.port.output.CounterIdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Adaptador de Geração de IDs")
class IdGeneratorAdapterTest {

  private static final long BLOCK_SIZE = 10L;

  @Mock
  private CounterIdRepository counterIdRepository;

  @Nested
  @DisplayName("Geração")
  class GenerateIdTests {

    @Test
    @DisplayName("Deve retornar IDs sequenciais dentro do bloco alocado")
    void shouldReturnSequentialIdsInsideAllocatedBlock() {
      // 1. Arrange
      when(counterIdRepository.allocateBlock(BLOCK_SIZE)).thenReturn(1L);
      var adapter = new IdGeneratorAdapter(counterIdRepository, BLOCK_SIZE);

      // 2. Act
      var firstId = adapter.generateId();
      var secondId = adapter.generateId();
      var thirdId = adapter.generateId();

      // 3. Assert
      assertThat(firstId).isEqualTo(1L);
      assertThat(secondId).isEqualTo(2L);
      assertThat(thirdId).isEqualTo(3L);
      verify(counterIdRepository).allocateBlock(BLOCK_SIZE);
      verifyNoMoreInteractions(counterIdRepository);
    }

    @Test
    @DisplayName("Deve alocar novo bloco somente quando o deslocamento atingir o tamanho do bloco")
    void shouldAllocateNewBlockOnlyWhenOffsetReachesBlockSize() {
      // 1. Arrange
      when(counterIdRepository.allocateBlock(BLOCK_SIZE)).thenReturn(1L, 11L);
      var adapter = new IdGeneratorAdapter(counterIdRepository, BLOCK_SIZE);

      // 2. Act
      var generatedIds = LongStream.rangeClosed(1, 11)
          .map(ignored -> adapter.generateId())
          .boxed()
          .toList();

      // 3. Assert
      assertThat(generatedIds)
          .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
      verify(counterIdRepository, times(2)).allocateBlock(BLOCK_SIZE);
      verifyNoMoreInteractions(counterIdRepository);
    }

    @Test
    @DisplayName("Deve gerar IDs únicos e sequenciais com chamadas concorrentes")
    void shouldGenerateUniqueSequentialIdsWithConcurrentCalls() throws Exception {
      // 1. Arrange
      var totalIds = 50;
      when(counterIdRepository.allocateBlock(BLOCK_SIZE))
          .thenReturn(1L, 11L, 21L, 31L, 41L);
      var adapter = new IdGeneratorAdapter(counterIdRepository, BLOCK_SIZE);
      var executor = Executors.newFixedThreadPool(10);
      var startLatch = new CountDownLatch(1);
      var tasks = new ArrayList<Callable<Long>>();

      for (int i = 0; i < totalIds; i++) {
        tasks.add(() -> {
          startLatch.await();
          return adapter.generateId();
        });
      }

      try {
        // 2. Act
        var futures = tasks.stream()
            .map(executor::submit)
            .toList();
        startLatch.countDown();

        var generatedIds = new ArrayList<Long>();
        for (var future : futures) {
          generatedIds.add(future.get(2, TimeUnit.SECONDS));
        }

        // 3. Assert
        assertThat(generatedIds).hasSize(totalIds);
        assertThat(new HashSet<>(generatedIds)).hasSize(totalIds);
        assertThat(generatedIds)
            .containsExactlyInAnyOrderElementsOf(LongStream.rangeClosed(1, totalIds)
                .boxed()
                .toList());
        verify(counterIdRepository, times(5)).allocateBlock(BLOCK_SIZE);
        verifyNoMoreInteractions(counterIdRepository);
      } finally {
        executor.shutdownNow();
      }
    }
  }
}
