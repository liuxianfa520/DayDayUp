package com.anxiaole.easyopen.interceptors.adminLogin;

import com.anxiaole.easyopen.interceptors.login.IgnoreLoginInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see IgnoreLoginInterceptor
 * @author LiuXianfa
 * 
 * @date 12/4 13:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreAdminLoginInterceptor {

    String usage = "如果一个api类上使用了 @UseAdminLoginInterceptor ,但是又需要忽略此api中单独的一个方法,则可使用此注解!" +
            "(此注解只能使用在方法上:如果想要对类中所有方法忽略登录拦截器,只需要在此类上不标注 @UseAdminLoginInterceptor)";
}
