package com.anxiaole.easyopen.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当 配置 easyopen.api-interceptor.enable-interceptors 配置【任意一个拦截器】时，条件匹配.
 *
 * <pre>
 *
 * 要实现的功能类似于:
 * {@code
 *   @ConditionalOnExpression("'${easyopen.api-interceptor.enable-interceptors}'.toLowerCase().contains('apipermissioninterceptor')
 *                             || '${easyopen.api-interceptor.enable-interceptors}'.toLowerCase().contains('tenantchooseinterceptor')")
 * }
 * </pre>
 *
 * @author LiuXianfa
 * 
 * @date 12/30 17:39
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnEnableAnyInterceptorsCondition.class)
public @interface ConditionalOnEnableAnyInterceptors {

    String usage = "当 配置 easyopen.api-interceptor.enable-interceptors 配置【任意一个拦截器】时，条件匹配.";

    /**
     * 启用的拦截器类名.
     */
    String[] value() default {};

}
