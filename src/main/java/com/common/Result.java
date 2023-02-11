package com.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    private static final int ERROR = -1;

    private boolean success;
    private int code;
    private T data;
    private String message;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result<String> success() {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        result.setCode(SUCCESS);
        result.setData("success");
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(FAIL);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(ERROR);
        result.setMessage(message);
        return result;
    }

}