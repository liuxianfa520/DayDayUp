package com.anxiaole.easyopen.interceptors.adminLogin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiuXianfa
 * 
 * @date 2020-12-01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseAdminLoginInterceptor {

    String usage = "在api类或方法上,使用此注解:则方法会被登录拦截器:AdminLoginInterceptor拦截.";

}
