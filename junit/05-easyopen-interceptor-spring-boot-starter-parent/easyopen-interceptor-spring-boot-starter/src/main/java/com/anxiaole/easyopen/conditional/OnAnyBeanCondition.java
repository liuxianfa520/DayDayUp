package com.anxiaole.easyopen.conditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * @author LiuXianfa
 * 
 * @date 12/4 22:31
 */
public class OnAnyBeanCondition implements Condition {
    private static final Logger logger = LoggerFactory.getLogger(OnAnyBeanCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        try {
            MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnAnyBean.class.getName());
            for (Class beanClass : ((Class[]) attrs.get("value").get(0))) {
                String[] beanNamesForType = beanFactory.getBeanNamesForType(beanClass, true, false);
                if (beanNamesForType.length > 0) {
                    logger.info("@ConditionalOnAnyBean({}.class) return true.", beanClass.getName());
                    return true;
                } else {
                    logger.info("@ConditionalOnAnyBean >>> Class [{}] not exist.", beanClass.getName());
                }
            }
        } catch (Exception e) {
            logger.error("@ConditionalOnAnyBean异常! 条件不匹配!", e);
        }
        return false;
    }
}
