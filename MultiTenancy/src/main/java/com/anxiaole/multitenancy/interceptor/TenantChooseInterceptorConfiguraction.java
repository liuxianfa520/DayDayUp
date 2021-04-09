package com.anxiaole.multitenancy.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/9 23:11
 */
@Component
public class TenantChooseInterceptorConfiguraction implements WebMvcConfigurer {

    @Autowired
    private TenantChooseInterceptor tenantChooseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantChooseInterceptor).addPathPatterns("/**");
    }

}
