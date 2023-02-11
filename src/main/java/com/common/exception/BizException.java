package com.common.exception;

/**
 * 自定义异常类
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BizException(String message, Throwable e) {
        super(message, e);
    }

    public BizException(String message) {
        super(message);
    }
}
