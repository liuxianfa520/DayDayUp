package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 条件序列化
 * <p>
 * 满足一定条件才会对此字段进行序列化
 *
 * @author AnXiaole
 * @date 2022/4/21 16:17
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
@JacksonAnnotationsInside
@JsonSerialize(using = ConditionalJsonSerializer.class)
public @interface ConditionalJsonSerialize {


    /**
     * 条件表达式,当表达式返回true时,才会对此字段序列化.否则不序列化
     * <pre>
     * 支持使用 ${} 引用spring的属性 比如: '${spring.profiles.active}' == 'dev'
     * 支持使用当前对象中的属性       比如:  name.equals('admin')
     * 支持使用当前对象中的方法       比如:  hello().equals('Hello,admin')
     * </pre>
     */
    String condition() default "true";
}