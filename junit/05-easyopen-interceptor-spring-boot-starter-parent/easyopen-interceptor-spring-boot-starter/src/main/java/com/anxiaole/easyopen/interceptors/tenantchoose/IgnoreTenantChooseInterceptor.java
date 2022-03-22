package com.anxiaole.easyopen.interceptors.tenantchoose;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略租户切换拦截器
 *
 * @author LiuXianfa
 * 
 * @date 2020-12-01
 * @see TenantChooseInterceptor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreTenantChooseInterceptor {

}
