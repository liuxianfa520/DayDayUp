package com.liuxianfa.junit.springboot.exception;

import com.liuxianfa.junit.springboot.R;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public R resolveException(HttpServletRequest request, BindException ex) {
        log.error("调用接口异常, url={}.", request.getRequestURI(), ex);
        return R.fail(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public R resolveException(HttpServletRequest request, Exception ex) {
        // 这里没有指定异常返回的错误页面， 默认都是ajax请求
        log.error("调用接口异常, url={}.", request.getRequestURI(), ex);
        if (ex instanceof ServletRequestBindingException) {
            return R.fail("参数绑定异常");
        }
        return R.fail("未知异常");
    }


}