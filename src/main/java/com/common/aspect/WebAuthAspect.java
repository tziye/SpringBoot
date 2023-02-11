package com.common.aspect;

import com.common.exception.BizException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Order(2)
@Aspect
@Component
public class WebAuthAspect {

    @Before("@annotation(com.common.aspect.WebAuth)")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        WebAuth annotation = method.getAnnotation(WebAuth.class);
        if (!Objects.equals("OK", annotation.value())) {
            throw new BizException("无权访问");
        }
    }
}
