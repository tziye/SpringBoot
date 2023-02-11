package com.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.RequestIpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Order(1)
@Aspect
@Component
@Slf4j(topic = "web-log")
public class WebLogAspect {

    @Autowired
    private ObjectMapper mapper;

    /**
     * 指定切点
     */
    @Pointcut(value = "execution(* com.controller..*.*Controller.*(..))")
    public void pointcut() {
    }

    /**
     * 打印请求和响应
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        log.info("【请求】url：{}，ip：{}，param：{}", request.getRequestURL(), RequestIpUtil.getRequestIp(request), pjp.getArgs());

        Class<?> clz = pjp.getTarget().getClass();
        String methodName = pjp.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info("【响应】class：{}，method：{}，cost：{}ms，result：{}", clz.getSimpleName(), methodName, end - start, result);
        return result;
    }

}
