package com.common.exception;

import com.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 全局异常处理类，只能处理通过controller层进来后产生的异常
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler
    public Result<String> bizException(BizException e) {
        log.error(e.getMessage(), e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler
    public Result<String> voValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        errorList.forEach(error -> errorMsg.append(error.getDefaultMessage()).append(";"));
        return Result.fail(errorMsg.toString());
    }

    @ExceptionHandler
    public Result<String> paramValidException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler
    public Result<String> unknownException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error("系统出现错误!");
    }

}
