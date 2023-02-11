package com.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomConstraintValidator.class)
public @interface CustomConstraint {

    String message() default "时间错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
