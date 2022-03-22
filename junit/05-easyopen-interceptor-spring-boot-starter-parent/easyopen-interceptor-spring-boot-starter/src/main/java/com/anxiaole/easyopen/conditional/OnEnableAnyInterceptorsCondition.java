package com.anxiaole.easyopen.conditional;

import com.anxiaole.easyopen.EasyOpenInterceptorProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class OnEnableAnyInterceptorsCondition implements Condition {
    private static final Logger logger = LoggerFactory.getLogger(OnEnableAnyInterceptorsCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            String propertyKey = String.format("%s.%s", EasyOpenInterceptorProperties.prefix, "enable-interceptors");
            String propertyKey2 = String.format("%s.%s", EasyOpenInterceptorProperties.prefix, "enableInterceptors");

            String enableInterceptors = context.getEnvironment().getProperty(propertyKey, context.getEnvironment().getProperty(propertyKey2));
            logger.info("EasyOpen拦截器自动配置-启用的拦截器:{}", enableInterceptors);
            enableInterceptors = StringUtils.trimAllWhitespace(enableInterceptors).toLowerCase();
            MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnEnableAnyInterceptors.class.getName());
            for (String className : ((String[]) attrs.get("value").get(0))) {
                if (enableInterceptors.contains(className.toLowerCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("@OnEnableAnyInterceptorsCondition! 条件不匹配!", e);
        }
        return false;
    }
}
