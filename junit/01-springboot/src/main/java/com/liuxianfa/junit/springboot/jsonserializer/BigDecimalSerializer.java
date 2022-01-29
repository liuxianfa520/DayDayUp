package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * BigDecimal json序列化器
 * <pre>
 * 可以自定义自己特定格式的bigDecimal 的序列化器
 * 使用方法:
 * {@code
 *     @JsonSerialize(using = BigDecimalSerializer.BigDecimalScale4Serializer.class)
 *     private BigDecimal dividendRatioBeforeTax;
 * }
 * </pre>
 *
 * @author LiuXianfa
 * @date 2022/1/12 21:43
 */
public interface BigDecimalSerializer {

    /**
     * BigDecimal类的JSON序列化:保留2位小数
     */
    class BigDecimalScale2Serializer extends StdSerializer<BigDecimal> {
        protected BigDecimalScale2Serializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeString("");
            } else {
                gen.writeNumber(value.setScale(2));
            }
        }
    }

    /**
     * BigDecimal类的JSON序列化:保留4位小数
     */
    class BigDecimalScale4Serializer extends StdSerializer<BigDecimal> {
        protected BigDecimalScale4Serializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeString("");
            } else {
                gen.writeNumber(value.setScale(4));
            }
        }
    }

    /**
     * BigDecimal类的JSON序列化:大金额转成    10,000.00   格式
     */
    DecimalFormat BIG_MONEY_DECIMAL_FORMAT = new DecimalFormat("#,###.00");

    class BigMoneySerializer extends StdSerializer<BigDecimal> {
        protected BigMoneySerializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeString("");
            } else {
                gen.writeString(BIG_MONEY_DECIMAL_FORMAT.format(value));
            }
        }
    }
}