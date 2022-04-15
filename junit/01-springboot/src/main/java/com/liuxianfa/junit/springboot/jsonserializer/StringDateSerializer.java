package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;

/**
 * 字符串类型的日期 序列化器
 *
 * @author xianfaliu
 * @date 2022/1/13 10:45
 */
public interface StringDateSerializer {
    Logger logger = LoggerFactory.getLogger(StringDateSerializer.class);

    /**
     * 把String类型的时间,格式化
     *
     * @param date   格式转换前的时间字符串
     * @param format 转换后格式
     * @return 格式化候的时间
     */
    static String patternDateString(String date, FastDateFormat format) {
        if (StrUtil.isEmpty(date)) {
            return "";
        }
        try {
            return DateUtil.parse(date).toString(format);
        } catch (Exception e) {
            logger.error(String.format("时间格式转换异常.需要转换的时间:[%s],格式:[%s]", date, format.getPattern()), e);
            return date;
        }
    }

    /**
     * 字符串日期格式json序列化成 [yyyy-MM-dd] 格式
     */
    class NormDateFormatJsonSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(patternDateString(value, DatePattern.NORM_DATE_FORMAT));
        }
    }


    /**
     * 字符串日期格式json序列化成 [yyyy年MM月dd日] 格式
     */
    class ChineseDatePatternJsonSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(patternDateString(value, DatePattern.CHINESE_DATE_FORMAT));
        }
    }

    /**
     * 字符串日期格式json序列化成 [MM-dd] 格式
     */
    String MONTH_AND_DAY_PATTERN = "MM-dd";

    class MonthAndDay extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(patternDateString(value, FastDateFormat.getInstance(MONTH_AND_DAY_PATTERN)));
        }
    }

    /**
     * 字符串日期格式json序列化成 [MM月dd日] 格式
     */
    String MONTH_AND_DAY_CN_PATTERN = "MM年dd日";

    class MonthAndDayCn extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(patternDateString(value, FastDateFormat.getInstance(MONTH_AND_DAY_CN_PATTERN)));
        }
    }

}
