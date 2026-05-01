package com.app.url_shortener.shared.exception.handler;

import com.app.url_shortener.shared.exception.AppBusinessException;
import com.app.url_shortener.shared.exception.CommonErrorCode;
import com.app.url_shortener.shared.exception.conflict.ConflictException;
import com.app.url_shortener.shared.exception.forbidden.ForbiddenException;
import com.app.url_shortener.shared.exception.notfound.NotFoundException;
import com.app.url_shortener.shared.exception.ratelimit.TooManyRequestsException;
import com.app.url_shortener.shared.exception.unauthorized.UnauthorizedException;
import com.app.url_shortener.shared.exception.validation.DomainValidationException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final String TYPE_VALIDATION = "/errors/validation";
  private static final String TYPE_CONFLICT = "/errors/conflict";
  private static final String TYPE_NOT_FOUND = "/errors/not-found";
  private static final String TYPE_UNAUTHORIZED = "/errors/unauthorized";
  private static final String TYPE_FORBIDDEN = "/errors/forbidden";
  private static final String TYPE_TOO_MANY_REQUESTS = "/errors/too-many-requests";
  private static final String TYPE_BUSINESS = "/errors/business";
  private static final String TYPE_INFRASTRUCTURE = "/errors/infrastructure";

  @ExceptionHandler(DomainValidationException.class)
  public ProblemDetail handleDomainValidation(DomainValidationException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, exception, "Validação");
    problemDetail.setType(URI.create(TYPE_VALIDATION));
    return problemDetail;
  }

  @ExceptionHandler(ConflictException.class)
  public ProblemDetail handleConflict(ConflictException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.CONFLICT, exception, "Conflito");
    problemDetail.setType(URI.create(TYPE_CONFLICT));
    return problemDetail;
  }

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleEntityNotFound(NotFoundException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.NOT_FOUND, exception, "Não encontrado");
    problemDetail.setType(URI.create(TYPE_NOT_FOUND));
    return problemDetail;
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ProblemDetail handleUnauthorized(UnauthorizedException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.UNAUTHORIZED, exception, "Não autorizado");
    problemDetail.setType(URI.create(TYPE_UNAUTHORIZED));
    return problemDetail;
  }

  @ExceptionHandler(ForbiddenException.class)
  public ProblemDetail handleForbidden(ForbiddenException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.FORBIDDEN, exception, "Proibido");
    problemDetail.setType(URI.create(TYPE_FORBIDDEN));
    return problemDetail;
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ProblemDetail handleTooManyRequests(TooManyRequestsException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatus.TOO_MANY_REQUESTS, exception, "Muitas requisições");
    problemDetail.setType(URI.create(TYPE_TOO_MANY_REQUESTS));
    return problemDetail;
  }

  @ExceptionHandler(AppBusinessException.class)
  public ProblemDetail handleBusinessException(AppBusinessException exception) {
    ProblemDetail problemDetail = buildProblemDetail(HttpStatusCode.valueOf(422), exception, "Negócio");
    problemDetail.setType(URI.create(TYPE_BUSINESS));
    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            CommonErrorCode.REQUEST_VALIDATION_FAILED.getMessage());
    problemDetail.setTitle("Validação");
    problemDetail.setType(URI.create(TYPE_VALIDATION));
    problemDetail.setProperty("errorCode", CommonErrorCode.REQUEST_VALIDATION_FAILED.getCode());

    List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
            .map(this::toFieldError)
            .toList();

    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }

  @ExceptionHandler({SdkException.class, DataAccessException.class})
  public ProblemDetail handleTechnicalDependencyFailure(Exception exception) {
    LOGGER.error("Falha técnica ao comunicar com dependência externa: {}", exception.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.SERVICE_UNAVAILABLE,
        CommonErrorCode.DEPENDENCY_FAILURE.getMessage());
    problemDetail.setTitle("Infraestrutura");
    problemDetail.setType(URI.create(TYPE_INFRASTRUCTURE));
    problemDetail.setProperty("errorCode", CommonErrorCode.DEPENDENCY_FAILURE.getCode());
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception exception) {
    LOGGER.error("Erro interno inesperado.", exception);
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    problemDetail.setTitle("Erro interno inesperado");
    problemDetail.setType(URI.create(TYPE_INFRASTRUCTURE));
    problemDetail.setProperty("errorCode", CommonErrorCode.INTERNAL_SERVER_ERROR.getCode());
    return problemDetail;
  }

  private Map<String, String> toFieldError(FieldError fieldError) {
    return Map.of(
            "field", fieldError.getField(),
            "message", fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage());
  }

  private ProblemDetail buildProblemDetail(HttpStatusCode status, AppBusinessException ex, String title) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    pd.setTitle(title);
    pd.setProperty("errorCode", ex.getErrorCode().getCode());
    return pd;
  }
}


