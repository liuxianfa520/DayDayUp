package com.liuxianfa.junit.springboot.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuxianfa.junit.springboot.jsonserializer.BigDecimalSerializer;
import com.liuxianfa.junit.springboot.jsonserializer.StringDateSerializer;
import com.liuxianfa.junit.springboot.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.core.date.DatePattern;
import lombok.Data;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/17 16:44
 */
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {
        helloService.hello(name);

        return "controller hello " + name;
    }


    @RequestMapping("/json")
    public JsonTestEntity json() {
        //   {
        //    "defaultDate": "2022-01-13T03:42:20.386+00:00",
        //    "dateCn": "2022年01月13日",
        //    "tradeDateString": "2021-05-03",
        //    "tradeDateCnString": "2021年05月03日",
        //    "monthAndDayString": "05-03",
        //    "money": 52.60,
        //    "bigMoney": "1,261,654,651.56",
        //    "navUnit": 1.0336,
        //    "nullBigDecimal": null,
        //    "nullBigDecimalNullsUsing": ""
        //   }
        return new JsonTestEntity();
    }

    @Data
    class JsonTestEntity {
        // SpringBoot默认序列化成 2022-01-13T03:42:20.386+00:00 格式
        Date defaultDate = new Date();

        @JsonFormat(pattern = DatePattern.CHINESE_DATE_PATTERN)
        Date dateCn = new Date();

        @JsonSerialize(using = StringDateSerializer.NORM_DATE_FORMAT.class)
        String tradeDateString = "20210503";

        @JsonSerialize(using = StringDateSerializer.CHINESE_DATE_PATTERN.class)
        String tradeDateCnString = "20210503";

        /**
         * 需要转成 05-03 格式
         */
        @JsonSerialize(using = StringDateSerializer.MonthAndDay.class)
        String monthAndDayString = "20210503";

        @JsonSerialize(using = BigDecimalSerializer.BigDecimalScale2Serializer.class)
        BigDecimal money = new BigDecimal("52.6");

        @JsonSerialize(using = BigDecimalSerializer.BigMoneySerializer.class)
        BigDecimal bigMoney = new BigDecimal("1261654651.56");

        /**
         * 单位净值    保留4位小数
         */
        @JsonSerialize(using = BigDecimalSerializer.BigDecimalScale4Serializer.class)
        BigDecimal navUnit = new BigDecimal("1.03360000");

        /**
         * SpringBoot默认输出 {"nullBigDecimal": null}
         *
         * bladex 默认输出  -1  详见文档: https://sns.bladex.vip/q-100.html
         */
        BigDecimal nullBigDecimal;

        /**
         * 使用 {@link JsonSerialize#nullsUsing()} 指定:当BigDecimal字段为null时,使用那种序列化格式.
         * 序列化后,会输出:   {"nullBigDecimalNullsUsing": ""}
         */
        @JsonSerialize(nullsUsing = BigDecimalSerializer.BigDecimalScale4Serializer.class)
        BigDecimal nullBigDecimalNullsUsing;
    }
}
