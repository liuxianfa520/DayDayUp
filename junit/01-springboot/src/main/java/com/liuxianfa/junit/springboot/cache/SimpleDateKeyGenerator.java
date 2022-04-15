package com.liuxianfa.junit.springboot.cache;

import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

/**
 * 缓存key生成器
 * <p>
 * 使用场景:如果方法参数是 java.util.Date 类型的,则把Date转成简单日期格式:yyyyMMdd格式
 *
 * @date 2022/4/15 16:52
 */
@Component
public class SimpleDateKeyGenerator extends SimpleKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 0) {
            return super.generate(target, method, params);
        }
        // 如果没有Date类型的参数,直接使用父类的逻辑
        if (Arrays.stream(params).noneMatch(param -> param instanceof Date)) {
            return super.generate(target, method, params);
        }

        StringBuilder key = new StringBuilder(method.getName()).append(":");
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Date) {
                key.append(DateUtil.format(((Date) params[i]), DatePattern.PURE_DATE_FORMAT));
            } else {
                key.append(String.valueOf(params[i]));
            }
            if (i != params.length - 1) {
                key.append(":");
            }
        }
        return key.toString();
    }
}