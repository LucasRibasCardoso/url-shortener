package com.app.url_shortener.iam.presentation.mapper;

import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequest;
import com.app.url_shortener.iam.presentation.dto.response.RegisterResponse;
import com.app.url_shortener.iam.presentation.dto.response.VerifyEmailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IamWebMapper {

  RegisterUserCommand toCommand(RegisterRequest request);

  VerifyEmailCommand toCommand(VerifyEmailRequest request);

  RegisterResponse toResponse(RegisterUserResult result);

  VerifyEmailResponse toResponse(VerifyEmailResult result);

  default VerificationCode mapToVerificationCode(String value) {
    if (value == null) return null;
    return VerificationCode.of(value);
  }

  default String mapToString(VerificationCode verificationCode) {
    if (verificationCode == null) return null;
    return verificationCode.value();
  }
}
