package com.app.url_shortener.url.application.port.output;

public interface CounterIdRepository {
  Long allocateBlock(long blockSize);
}
