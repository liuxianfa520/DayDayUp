package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @see ConditionalJsonSerialize
 * @date 2022/4/21 16:19
 */
@Slf4j
public class ConditionalJsonSerializer extends JsonSerializer implements ContextualSerializer {
    private static final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private BeanProperty beanProperty = null;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            serializers.findNullValueSerializer(beanProperty).serialize(value, gen, serializers);
            return;
        }
        if (beanProperty == null) {
            serializeValue(value, gen, serializers);
            return;
        }

        ConditionalJsonSerialize annotation = beanProperty.getAnnotation(ConditionalJsonSerialize.class);
        if (annotation == null) {
            serializeValue(value, gen, serializers);
            return;
        }

        try {
            String condition = annotation.condition();
            // 解析spring中的占位符
            String expression = SpringUtil.getApplicationContext().getEnvironment().resolvePlaceholders(condition);
            // spel      context设置的是当前序列化的对象.如果需要设置变量和方法,可以使用: org.springframework.expression.spel.support.StandardEvaluationContext
            Boolean eq = expressionParser.parseExpression(expression).getValue(gen.getCurrentValue(), Boolean.class);
            if (eq != null && eq) {
                serializeValue(value, gen, serializers);
            } else {
                serializers.findNullValueSerializer(beanProperty).serialize(value, gen, serializers);
            }
        } catch (Exception e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    private void serializeValue(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        serializers.findValueSerializer(value.getClass(), beanProperty).serialize(value, gen, serializers);
    }

    /**
     * http://www.liuhaihua.cn/archives/380858.html
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return prov.findNullValueSerializer(beanProperty);
        }
        ConditionalJsonSerializer serializer = new ConditionalJsonSerializer();
        serializer.beanProperty = beanProperty;
        return serializer;
    }
}
