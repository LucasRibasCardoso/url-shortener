package com.app.url_shortener.presentation.validator;

import com.app.url_shortener.presentation.validator.imp.HttpUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HttpUrlValidator.class)
public @interface ValidHttpUrl {
  String message() default "A URL informada é inválida";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
