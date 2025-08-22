package com.megastudy.surlkdh.common.validation;

import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class OneOfTwoUrlsValidator implements ConstraintValidator<OneOfTwoUrls, CreateShortUrlRequest> {

    @Override
    public void initialize(OneOfTwoUrls constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateShortUrlRequest request, ConstraintValidatorContext context) {
        boolean hasPcUrl = StringUtils.hasText(request.getPcOriginalUrl());
        boolean hasMobileUrl = StringUtils.hasText(request.getMobileOriginalUrl());

        if (!hasPcUrl && !hasMobileUrl) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("pcOriginalUrl")
                   .addConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("mobileOriginalUrl")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
