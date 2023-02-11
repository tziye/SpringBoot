package com.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.util.Date;

public class CustomConstraintValidator implements ConstraintValidator<CustomConstraint, Date> {

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        return value.after(Date.from(Instant.parse("1995-01-01T00:00:00.00Z")));
    }
}