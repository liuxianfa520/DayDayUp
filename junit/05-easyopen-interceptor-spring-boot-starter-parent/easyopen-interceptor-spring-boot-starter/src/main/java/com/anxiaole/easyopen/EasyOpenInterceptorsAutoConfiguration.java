package com.anxiaole.easyopen;

import com.anxiaole.easyopen.conditional.ConditionalOnAnyBean;
import com.anxiaole.easyopen.conditional.ConditionalOnEnableAnyInterceptors;
import com.anxiaole.easyopen.interceptors.adminLogin.AdminLoginInterceptor;
import com.anxiaole.easyopen.interceptors.adminLogin.CcLoginEasyOpenApiProperties;
import com.anxiaole.easyopen.interceptors.apipermission.ApiPermissionInterceptor;
import com.anxiaole.easyopen.interceptors.login.LoginInterceptor;
import com.anxiaole.easyopen.interceptors.tenantchoose.TenantChooseInterceptor;
import com.anxiaole.easyopen.interceptors.xss.XssInterceptor;

import com.xxl.conf.core.spring.XxlConfFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import lombok.Getter;
import lombok.Setter;

/**
 * EasyOpen拦截器自动配置
 *
 * @author LiuXianfa
 * 
 * @date 2020-12-01
 */
@Configuration
@ConditionalOnProperty(value = "easyopen.api-interceptor.enable", havingValue = "true")
@EnableConfigurationProperties({EasyOpenInterceptorProperties.class, EasyOpenInterceptorsAutoConfiguration.CcConfProperties.class, CcLoginEasyOpenApiProperties.class})
public class EasyOpenInterceptorsAutoConfiguration {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public EasyOpenInterceptorsApplicationListener xssInterceptorApplicationListener() {
        return new EasyOpenInterceptorsApplicationListener();
    }

    @Bean
    @Order(50)
    @ConditionalOnEnableAnyInterceptors({"TenantChooseInterceptor", "ApiPermissionInterceptor", "LoginInterceptor"})
    @ConditionalOnClass(name = {"com.anxiaole.udsc.config.NBEntHolder", "com.anxiaole.framework.redis.RedisPrefixHolder"})
    public TenantChooseInterceptor tenantChooseInterceptor() {
        return new TenantChooseInterceptor();
    }

    @Bean
    @Order(100)
    @ConditionalOnEnableAnyInterceptors({"XssInterceptor"})
    public XssInterceptor xssInterceptor() {
        return new XssInterceptor();
    }

    @Bean
    @Order(150)
    @ConditionalOnEnableAnyInterceptors({"LoginInterceptor"})
    @ConditionalOnClass(name = {"com.anxiaole.framework.redis.RedisUtil", "com.anxiaole.passport.util.LoginInfoUtils"})
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * {@link ApiPermissionInterceptor} 依赖于 {@link TenantChooseInterceptor}
     */
    @Bean
    @Order(200)
    @ConditionalOnEnableAnyInterceptors({"ApiPermissionInterceptor"})
    @ConditionalOnClass(name = {"com.anxiaole.framework.redis.RedisUtil", "com.anxiaole.passport.util.LoginInfoUtils"})
    public ApiPermissionInterceptor apiPermissionInterceptor() {
        return new ApiPermissionInterceptor();
    }

    @Bean
    @ConditionalOnEnableAnyInterceptors({"AdminLoginInterceptor"})
    public AdminLoginInterceptor adminLoginInterceptor() {
        return new AdminLoginInterceptor();
    }

    @Bean
    @ConditionalOnAnyBean({TenantChooseInterceptor.class, LoginInterceptor.class, ApiPermissionInterceptor.class}) //指定了哪些Interceptor需要依赖cc-conf
    public XxlConfFactory xxlConfFactory(CcConfProperties ccConfProperties) {
        XxlConfFactory xxlConf = new XxlConfFactory();
        xxlConf.setBeanName("xxlConfFactory");
        xxlConf.setZkaddress(ccConfProperties.getZkaddress());
        xxlConf.setZkpath(ccConfProperties.getZkpath());
        xxlConf.setMirrorfile(ccConfProperties.getMirrorfile());
        return xxlConf;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "cc.conf")
    public static class CcConfProperties {
        private static final String defaultZkAddress = "zk-0:2181,zk-1:2181,zk-2:2181";
        private static final String defaultZkRootPath = "/nb-conf";
        private static final String defaultMirrorFile = "/apps/logs/ccConf/cc-conf-mirror.properties";

        private String zkaddress = defaultZkAddress;
        private String zkpath = defaultZkRootPath;
        private String mirrorfile = defaultMirrorFile;
    }
}