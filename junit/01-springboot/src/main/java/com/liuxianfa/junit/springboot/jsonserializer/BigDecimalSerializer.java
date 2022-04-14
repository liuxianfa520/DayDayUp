package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

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
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/12 21:43
 */
public interface BigDecimalSerializer {
    /**
     * 当bigdecimal值为null时,默认输出什么
     */
    String BIG_DECIMAL_NULL_VALUE = "";

    /**
     * BigDecimal类的JSON序列化:大金额转成    10,000.00   格式
     */
    DecimalFormat BIG_MONEY_DECIMAL_FORMAT = new DecimalFormat("#,###.00");

    /**
     * 百分之0
     */
    String ZERO_PERCENTAGE = "0.00";

    /**
     * 百分号
     */
    String PERCENT_SIGN = "%";

    String MILLION = "万元";


    /**
     * BigDecimal类的JSON序列化:使用四舍五入保留2位小数 {@link RoundingMode#HALF_UP}
     */
    class BigDecimalScale2Serializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(bigDecimalToString(value, 2, RoundingMode.HALF_UP));
        }
    }

    /**
     * BigDecimal类的JSON序列化:保留4位小数
     */
    class BigDecimalScale4Serializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(bigDecimalToString(value, 4, null));
        }
    }

    /**
     * BigDecimal类的JSON序列化:保留4位小数 并 使用 {@link RoundingMode#HALF_UP}
     */
    class BigDecimalScale4RoundDownSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(bigDecimalToString(value, 4, RoundingMode.HALF_UP));
        }
    }

    /**
     * BigDecimal类的JSON序列化:大金额转成    10,000.00   格式
     */
    class BigMoneySerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(BIG_MONEY_DECIMAL_FORMAT.format(value));
        }
    }

    /**
     * BigDecimal类的JSON序列化:金额(单位:万元)
     * <p>
     * 比如 new BigDecimal(1000)  序列化成字符串:  "1000万元"
     */
    class BigMoneyMillionSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(bigDecimalToString(value, 0, RoundingMode.HALF_UP, "0").concat(MILLION));
        }
    }

    /**
     * 百分比
     * <p>
     * 四舍五入保留2位小数,并拼接 % 百分号
     * <p>
     * 如果百分比为null,则返回 0.00%
     */
    class PercentageSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(bigDecimalToString(value, 2, RoundingMode.HALF_UP, ZERO_PERCENTAGE).concat(PERCENT_SIGN));
        }
    }

    /**
     * BigDecimal 类型转 string 字符串
     *
     * @param value
     * @param scale        保留小数
     * @param roundingMode 小数四舍五入模式
     * @return
     */
    static String bigDecimalToString(BigDecimal value, int scale, RoundingMode roundingMode) {
        return bigDecimalToString(value, scale, roundingMode, BIG_DECIMAL_NULL_VALUE);
    }

    /**
     * BigDecimal 类型转 string 字符串
     *
     * @param value
     * @param scale        保留小数
     * @param roundingMode 小数四舍五入模式
     * @param defaultValue 默认值
     * @return
     */
    static String bigDecimalToString(BigDecimal value, int scale, RoundingMode roundingMode, String defaultValue) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        if (Objects.isNull(roundingMode)) {
            return value.setScale(scale, RoundingMode.HALF_UP).toPlainString();
        } else {
            return value.setScale(scale, roundingMode).toPlainString();
        }
    }

    /**
     * BigDecimal 类型转 string 字符串
     *
     * @param value        BigDecimal值
     * @param scale        保留小数
     * @param roundingMode 小数舍入模式. 默认 四舍五入 :{@link RoundingMode#HALF_UP}
     * @param defaultValue 默认值
     * @param suffix       后缀
     * @return
     */
    static String bigDecimalToString(BigDecimal value, int scale, RoundingMode roundingMode, String defaultValue, String suffix) {
        if (suffix == null) {
            suffix = "";
        }
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        if (Objects.isNull(roundingMode)) {
            return value.setScale(scale, RoundingMode.HALF_UP).toPlainString() + suffix;
        } else {
            return value.setScale(scale, roundingMode).toPlainString() + suffix;
        }
    }
}