package com.anxiaole.multitenancy.lazyLoadUseAop;

import com.anxiaole.multitenancy.exception.CreateDataSourceException;
import com.anxiaole.multitenancy.utils.TenantIdHolder;

import org.I0Itec.zkclient.ZkClient;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.Objects;

import javax.sql.DataSource;

import lombok.SneakyThrows;

/**
 * <pre>
 *
 * 这是一种错误的实现.
 *
 * 并不能使用aop拦截到 determineTargetDataSource 方法的调用:
 * 因为 determineTargetDataSource 方法的调用是目标对象使用 this.determineTargetDataSource 调用的.
 *
 *
 * 反思:
 * 在目标对象里使用this调用的方法,无法被aop增强!!!
 * </pre>
 *
 * @author LiuXianfa
 * 
 * @date 4/11 1:43
 */
@Configurable
public class AopConfig {
    private static final Logger log = LoggerFactory.getLogger(AopConfig.class);
    private static final String TARGET_METHOD_NAME = "determineTargetDataSource";

    @SneakyThrows
    @Bean
    public ProxyFactoryBean routingDataSourceProxy(ZkClient zkClient) {
        LazyLoadUseAopRoutingDataSource target = new LazyLoadUseAopRoutingDataSource(zkClient);
        target.setTargetDataSources(Collections.emptyMap());

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(target);
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.addAdvice((MethodInterceptor) methodInvocation -> {
            if (!Objects.equals(methodInvocation.getMethod().getName(), TARGET_METHOD_NAME)) {
                log.info(String.format("目标对象的[%s]方法被调用.不是[%s]方法,直接放行.", methodInvocation.getMethod().getName(), TARGET_METHOD_NAME));
                return methodInvocation.proceed();
            }

            DataSource dataSource;
            try {
                // 懒加载租户数据源:程序第一次使用tenantId=1的数据源时会报错 IllegalStateException.
                // 报错时,去初始化租户的数据源并且重新初始化 RoutingDataSource
                dataSource = (DataSource) methodInvocation.proceed();
            } catch (IllegalStateException e) {
                log.warn(String.format("当前租户的数据源尚未初始化.现在去初始化.tenantId=[%s].", TenantIdHolder.getTenantId()), e);
                dataSource = target.reloadTenantDataSource();
                if (dataSource == null) {
                    throw new CreateDataSourceException(TenantIdHolder.getTenantId());
                }
            }
            return dataSource;
        });
        return proxyFactoryBean;
    }

}
