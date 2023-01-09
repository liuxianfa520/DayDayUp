package com.liuxianfa.junit.springboot.jsonserializer.desensitization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PrivacySerializer extends JsonSerializer<String> implements ContextualSerializer {

    // 脱敏类型
    private DesensitizedUtil.DesensitizedType desensitizedType;

    @Override
    public void serialize(final String origin, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        if (StrUtil.isNotBlank(origin) && null != desensitizedType) {
            switch (desensitizedType) {
                case CHINESE_NAME:
                    jsonGenerator.writeString(DesensitizedUtil.chineseName(origin));
                    break;
                case ID_CARD:
                    jsonGenerator.writeString(DesensitizedUtil.idCardNum(origin, 3, 3));
                    break;
                case MOBILE_PHONE:
                    jsonGenerator.writeString(DesensitizedUtil.mobilePhone(origin));
                    break;
                case FIXED_PHONE:
                    jsonGenerator.writeString(DesensitizedUtil.fixedPhone(origin));
                    break;
                case EMAIL:
                    jsonGenerator.writeString(DesensitizedUtil.email(origin));
                    break;
                case PASSWORD:
                    jsonGenerator.writeString(DesensitizedUtil.password(origin));
                    break;
                case CAR_LICENSE:
                    jsonGenerator.writeString(DesensitizedUtil.carLicense(origin));
                    break;
                case BANK_CARD:
                    jsonGenerator.writeString(DesensitizedUtil.bankCard(origin));
                    break;
                case ADDRESS:
                    jsonGenerator.writeString(DesensitizedUtil.address(origin, 8));
                    break;
                case USER_ID:
                    jsonGenerator.writeString(DesensitizedUtil.userId().toString());
                    break;
                default:
                    throw new IllegalArgumentException("unknown privacy type enum " + desensitizedType);
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,
                                              final BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                PrivacyEncrypt privacyEncrypt = beanProperty.getAnnotation(PrivacyEncrypt.class);
                if (privacyEncrypt == null) {
                    privacyEncrypt = beanProperty.getContextAnnotation(PrivacyEncrypt.class);
                }
                if (privacyEncrypt != null) {
                    return new PrivacySerializer(privacyEncrypt.type());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}