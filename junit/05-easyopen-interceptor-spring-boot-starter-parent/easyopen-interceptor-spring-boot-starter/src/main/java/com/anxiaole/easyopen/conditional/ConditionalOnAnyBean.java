package com.anxiaole.easyopen.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当BeanFactory中包含【任意一个bean】时，条件匹配.
 *
 * @author LiuXianfa
 * 
 * @date 12/4 22:39
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnAnyBeanCondition.class)
public @interface ConditionalOnAnyBean {

    String usage = "当BeanFactory中包含【任意一个bean】时，条件匹配.";

    /**
     * 需要存在的bean的类类型。
     */
    Class<?>[] value() default {};

}
