package com.megastudy.surlkdh.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfTwoUrlsValidator.class)
public @interface OneOfTwoUrls {
	String message() default "PC 원본 URL 또는 모바일 원본 URL 중 하나는 필수입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
