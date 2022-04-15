package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指定字段的枚举类
 *
 * @author xianfaliu
 * @date 2022/4/14 17:01
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
@JacksonAnnotationsInside
@JsonSerialize(using = EnumsSerializer.StringKeyEnumsSerializer.class)
public @interface JsonEnumSerialize {

    /**
     * 指定枚举类
     * <pre>
     * 仅支持以下几种字段的枚举:
     * value     cnName
     * value     name
     * code      remark
     * </pre>
     */
    Class<?> enumClass();


    /**
     * 枚举输出的新字段名称
     *
     * <pre>
     * 比如  SingleProduct#productStatus 字段是个枚举,要json序列化给前端,需要添加一个 productStatusDesc 的字段,
     * 就设置 fieldName = "productStatusDesc"
     *
     * 如果不设置,则默认自动添加一个 {原字段名}+"Desc"  的新字段
     * </pre>
     */
    String fieldName() default "";

}