package com.nuo.nuopaoserver.exception;

import com.nuo.nuopaoserver.common.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(BizException.class)
    public Result handleException(BizException e) {
        return Result.fail( e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleException(MethodArgumentNotValidException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return Result.fail("系统错误"+e.getMessage());
    }
}
