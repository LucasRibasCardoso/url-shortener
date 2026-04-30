package com.app.url_shortener.auth.presentation.mapper;

import com.app.url_shortener.auth.application.command.RegisterUserCommand;
import com.app.url_shortener.auth.application.command.VerifyEmailCommand;
import com.app.url_shortener.auth.application.result.RegisterUserResult;
import com.app.url_shortener.auth.application.result.VerifyEmailResult;
import com.app.url_shortener.auth.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.auth.presentation.dto.request.VerifyEmailRequest;
import com.app.url_shortener.auth.presentation.dto.response.RegisterResponse;
import com.app.url_shortener.auth.presentation.dto.response.VerifyEmailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuthWebMapper {

  RegisterUserCommand toCommand(RegisterRequest request);

  VerifyEmailCommand toCommand(VerifyEmailRequest request);

  RegisterResponse toResponse(RegisterUserResult result);

  VerifyEmailResponse toResponse(VerifyEmailResult result);
}
