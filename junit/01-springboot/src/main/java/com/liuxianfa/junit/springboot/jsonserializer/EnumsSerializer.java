package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;

/**
 * 枚举 序列化器
 *
 * @author xianfaliu2@creditease.cn
 * @date 2022/4/12 15:45
 */
public interface EnumsSerializer {
    Logger logger = LoggerFactory.getLogger(EnumsSerializer.class);

    /**
     * 字符串日期格式json序列化成 [yyyy-MM-dd] 格式
     */
    class StringKeyEnumsSerializer extends JsonSerializer<String> implements ContextualSerializer {
        private BeanProperty beanProperty = null;

        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (StrUtil.isEmpty(value) || beanProperty == null) {
                gen.writeString("");
                return;
            }
            // 先把原始值写到json字符串中
            gen.writeString(value);

            // 再根据枚举key,解析枚举中文描述.
            JsonEnumSerialize jsonEnumSerialize = beanProperty.getAnnotation(JsonEnumSerialize.class);
            if (jsonEnumSerialize == null) {
                return;
            }
            String fieldName = StrUtil.isEmpty(jsonEnumSerialize.fieldName()) ? beanProperty.getName() + "Desc" : jsonEnumSerialize.fieldName();
            gen.writeStringField(fieldName, getEnumCnDesc(value, jsonEnumSerialize));
        }

        /**
         * http://www.liuhaihua.cn/archives/380858.html
         */
        @Override
        public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty beanProperty) throws JsonMappingException {
            if (beanProperty == null) {
                return prov.findNullValueSerializer(beanProperty);
            }
            StringKeyEnumsSerializer serializer = new StringKeyEnumsSerializer();
            serializer.beanProperty = beanProperty;
            return serializer;
        }
    }

    /**
     * 根据枚举key获取中文描述
     *
     * @param value             枚举key值
     * @param jsonEnumSerialize 枚举class
     * @return 枚举的中文描述
     */
    static String getEnumCnDesc(String value, JsonEnumSerialize jsonEnumSerialize) {
        // 没有指定枚举,返回原字符串
        if (jsonEnumSerialize == null) {
            return value;
        }
        // 指定的class不是枚举,返回原字符串
        if (!jsonEnumSerialize.enumClass().isEnum()) {
            return value;
        }

        try {
            Object[] enumConstants = jsonEnumSerialize.enumClass().getEnumConstants();
            if (enumConstants != null && enumConstants.length > 0) {
                for (Object enumConstant : enumConstants) {
                    if (Objects.equals(invokeEnumMethod(enumConstant, "getCode"), value)) {
                        return String.valueOf(invokeEnumMethod(enumConstant, "getRemark"));
                    } else if (Objects.equals(invokeEnumMethod(enumConstant, "getValue"), value)) {
                        return String.valueOf(invokeEnumMethod(enumConstant, "getCnName"));
                    } else if (Objects.equals(invokeEnumMethod(enumConstant, "getValue"), value)) {
                        return String.valueOf(invokeEnumMethod(enumConstant, "getCnName", "getName"));
                    }
                }
            }
        } catch (Exception e) {
            return value;
        }
        return value;
    }


    /**
     * 调用此枚举对象的指定方法
     *
     * @param enumObject  枚举对象
     * @param methodNames 枚举方法
     * @return 指定方法的返回值
     */
    static String invokeEnumMethod(Object enumObject, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = enumObject.getClass().getMethod(methodName);
                Object invoke = method.invoke(enumObject);
                return String.valueOf(invoke);
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException();
    }
}