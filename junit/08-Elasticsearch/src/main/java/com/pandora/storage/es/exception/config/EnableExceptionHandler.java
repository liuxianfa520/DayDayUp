package com.pandora.storage.es.exception.config;


import com.pandora.storage.es.exception.MeheExceptionHandler;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 启用异常处理器 {@link MeheExceptionHandler}
 *
 * 使用方法：
 * 1、引入此依赖
 * {@code
 *
 *         <dependency>
 *             <groupId>com.pandora</groupId>
 *             <artifactId>mehe-common</artifactId>
 *             <version>${mehe-common.version}</version>
 *         </dependency>
 * }
 *
 * 2、在SpringBoot Application 类上使用此注解。
 *
 * 3、此时在controller中不用使用try-catch来处理异常，而是交给此异常处理统一处理。
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/5/13 14:27
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(ExceptionHandlerConfiguration.class)
public @interface EnableExceptionHandler {

}
