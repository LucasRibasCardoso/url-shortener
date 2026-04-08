package com.app.url_shortener.application.port.output;

public interface CounterIdRepository {
  Long allocateBlock(long blockSize);
}
