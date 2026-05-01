package com.app.url_shortener.iam.application.port.output;

import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;

public interface IssueAccessTokenPort {

  String getToken(AuthenticatedUserResult user);

  long getExpiresInMinutes();
}
