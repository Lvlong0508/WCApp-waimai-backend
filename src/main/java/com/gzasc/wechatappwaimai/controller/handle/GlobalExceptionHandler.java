package com.gzasc.wechatappwaimai.controller.handle;

import cn.dev33.satoken.exception.NotLoginException;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public WcwmResponse<?> handleNotLogin(NotLoginException e) {
        return WcwmResponse.error(401, "未登录或会话已过期");
    }

    @ExceptionHandler(RuntimeException.class)
    public WcwmResponse<?> handleRuntime(RuntimeException e) {
        return WcwmResponse.error(500, e.getMessage());
    }
}