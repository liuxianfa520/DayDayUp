package com.pandora.storage.es.exception;


import com.pandora.storage.es.util.HttpContextUtils;
import com.pandora.storage.es.util.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;

import cn.hutool.core.exceptions.ValidateException;


/**
 * 异常处理器
 *
 * @author liuxianfa
 * @email xianfaliu@newbanker.cn
 * @since 1.0.0 2018-04-10
 */
@RestControllerAdvice
public class MeheExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    public String applicationName;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(MeheException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleStorageException(MeheException e) {
        logger.error(">>>StorageException:{}", e.getMsg(), e);
        HttpContextUtils.getResponse().setStatus(e.getHttpCode());
        return R.error(e.getCode(), e.getMsg())
                .setApplicationName(applicationName);
    }

    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleValidateException(ValidateException e) {
        logger.warn("数据Validate未通过：" + e.getMessage(), e);
        return R.error(500, e.getMessage())
                .setApplicationName(applicationName);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleUnsupportedEncodingException(UnsupportedEncodingException e) {
        logger.error(e.getMessage(), e);
        return R.error("URLDecoder异常，请联系管理员！")
                .put("errorDetail", e.getMessage())
                .setApplicationName(applicationName);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleException(HttpMessageNotReadableException e) {
        logger.error(">>>HttpMessageNotReadableException:{}", e.getMessage(), e);
        return R.error("参数转换异常！")
                .put("errorDetail", e.getMessage())
                .setApplicationName(applicationName);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleException(HttpRequestMethodNotSupportedException e) {
        logger.error(">>>HttpRequestMethodNotSupportedException:{}", e.getMessage(), e);
        return R.error(e.getMessage())
                .setApplicationName(applicationName);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error().put("errorDetail", e.getMessage())
                .setApplicationName(applicationName);
    }
}
