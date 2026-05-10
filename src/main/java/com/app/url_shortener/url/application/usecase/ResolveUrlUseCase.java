package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.application.command.ResolveUrlCommand;
import com.app.url_shortener.url.application.result.ResolvedUrlResult;

public interface ResolveUrlUseCase {
  ResolvedUrlResult execute(ResolveUrlCommand command);
}
