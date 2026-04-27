package com.app.url_shortener.url.application.service.imp;

import com.app.url_shortener.url.application.port.output.CounterIdRepository;
import com.app.url_shortener.url.application.service.IdGeneratorService;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorServiceImp implements IdGeneratorService {

  private final CounterIdRepository counterIdRepository;
  private final long blockSize;

  private volatile long baseId = 0;
  private final AtomicLong offset = new AtomicLong(0);

  public IdGeneratorServiceImp(
      CounterIdRepository counterIdRepository,
      @Value("${app.id-generator.block-size}") long blockSize) {
    this.blockSize = blockSize;
    this.counterIdRepository = counterIdRepository;
    this.offset.set(blockSize);
  }

  @Override
  public long generateId() {
    while (true) {
      long currentOffset = offset.getAndIncrement();

      if (currentOffset < blockSize) {
        return baseId + currentOffset;
      }
      allocateBlock();
    }
  }

  private synchronized void allocateBlock() {
    if (offset.get() >= blockSize) {
      baseId = counterIdRepository.allocateBlock(blockSize);
      offset.set(0);
    }
  }
}
