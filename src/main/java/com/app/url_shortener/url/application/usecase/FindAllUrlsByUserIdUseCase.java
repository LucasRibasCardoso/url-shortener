package com.app.url_shortener.url.application.usecase;

import com.app.url_shortener.url.application.command.FindAllUrlsByUserIdCommand;
import com.app.url_shortener.url.application.result.PageUrlResult;

public interface FindAllUrlsByUserIdUseCase {

  PageUrlResult execute(FindAllUrlsByUserIdCommand command);
}
