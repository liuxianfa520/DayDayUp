package com.anxiaole.easyopen;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 12/1 11:15
 */
@Data
@ConfigurationProperties(prefix = EasyOpenInterceptorProperties.prefix)
public class EasyOpenInterceptorProperties {

    public static final String prefix = "easyopen.api-interceptor";

    /**
     * 是否启用拦截器自动配置
     */
    private boolean enable;

    /**
     * 启用的拦截器字符串,使用英文逗号分隔
     *
     * <pre>
     * 举例:
     * easyopen.api-interceptor.enable=true
     * easyopen.api-interceptor.enable-interceptors=tenantChooseInterceptor,xssFilter
     * </pre>
     */
    private String enableInterceptors;

}
