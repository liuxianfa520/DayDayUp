package com.anxiaole.easyopen.interceptors.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用xss拦截器
 *
 * @author LiuXianfa
 * 
 * @date 11/25 16:49
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseXssInterceptor {

}