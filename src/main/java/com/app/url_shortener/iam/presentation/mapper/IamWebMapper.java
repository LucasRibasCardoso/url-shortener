package com.app.url_shortener.iam.presentation.mapper;

import com.app.url_shortener.iam.application.command.RefreshTokenCommand;
import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.result.RefreshTokenResult;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.LoginRequestDto;
import com.app.url_shortener.iam.presentation.dto.response.RefreshTokenResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.RegisterResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.VerifyEmailResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.LoginResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IamWebMapper {

  RegisterUserCommand toCommand(RegisterRequestDto request);
  RegisterResponseDto toResponse(RegisterUserResult result);

  VerifyEmailCommand toCommand(VerifyEmailRequestDto request);
  VerifyEmailResponseDto toResponse(VerifyEmailResult result);

  LoginCommand toCommand(LoginRequestDto request);
  LoginResponseDto toResponse(LoginResult result);

  RefreshTokenResponseDto toResponse(RefreshTokenResult result);

  default VerificationCode mapToVerificationCode(String value) {
    if (value == null) return null;
    return VerificationCode.of(value);
  }

  default String mapToString(VerificationCode verificationCode) {
    if (verificationCode == null) return null;
    return verificationCode.value();
  }
}
