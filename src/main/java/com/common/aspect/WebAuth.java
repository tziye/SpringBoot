package com.common.aspect;

import javax.persistence.Inheritance;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inheritance
public @interface WebAuth {

    String value() default "";
}
