package com.liuxianfa.junit.springboot.datasources.aspect;


import com.liuxianfa.junit.springboot.datasources.DynamicDataSource;
import com.liuxianfa.junit.springboot.datasources.annotation.DataSourceNames;
import com.liuxianfa.junit.springboot.datasources.annotation.DataSourceRouting;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;


/**
 * 多数据源，切面处理类
 */
@Slf4j
@Aspect
@Component
public class DataSourceAspect implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String USER_MAPPER_PACKAGE = "com.liuxianfa.junit.springboot.user.dao";
    public static final String ORDER_MAPPER_PACKAGE = "com.liuxianfa.junit.springboot.order.dao";

    /**
     * 恒生数据库mapper包名
     */
    public static final String HENGSHENG_MAPPER_PACKAGE = "com.mam.hengsheng.dao.mapper";

    @Pointcut("execution(* com.liuxianfa.junit.springboot.*.dao.*Mapper.*(..))")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSourceRouting ds = method.getAnnotation(DataSourceRouting.class);
        if (ds != null) {
            // 目标方法上主动声明了注解,则就使用主动声明的数据源
            DynamicDataSource.setDataSource(ds.name());
        } else {
            // 根据类所在包名确定使用哪种数据源
            String className = getClassName(point);
            if (className.contains(USER_MAPPER_PACKAGE)) {
                DynamicDataSource.setDataSource(DataSourceNames.USER);
            } else {
                DynamicDataSource.setDataSource(DataSourceNames.ORDER);
            }
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 获取当前target类的全限定类名
     */
    private String getClassName(ProceedingJoinPoint point) {
        try {
            if (AopUtils.getTargetClass(point.getTarget()).getGenericInterfaces().length != 0) {
                return AopUtils.getTargetClass(point.getTarget()).getGenericInterfaces()[0].getTypeName();
            } else {
                return AopUtils.getTargetClass(point.getTarget()).getTypeName();
            }
        } catch (Exception e) {
            String name = point.getSignature().getDeclaringType().getName();
            return name.isEmpty() ? "" : name;
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
