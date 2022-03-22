package com.anxiaole.easyopen.interceptors.apipermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果一个api类上使用了 @UseApiPermissionInterceptor ,但是又需要忽略此api中单独的一个方法,则可使用此注解!
 * (此注解只能使用在方法上,如果想要对整个类忽略登录拦截器,只需要在此类上不要标注 @UseApiPermissionInterceptor 即可.)
 *
 * @author LiuXianfa
 * 
 * @date 2020-12-4 15:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreApiPermissionInterceptor {

    String usage = "如果一个api类上使用了 @UseApiPermissionInterceptor ,但是又需要忽略此api中单独的一个方法,则可使用此注解!" +
            "(此注解只能使用在方法上:如果想要对类中所有方法忽略登录拦截器,只需要在此类上不标注 @UseApiPermissionInterceptor)";
}
