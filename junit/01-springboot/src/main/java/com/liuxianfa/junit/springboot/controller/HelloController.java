package com.liuxianfa.junit.springboot.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuxianfa.junit.springboot.entity.Address;
import com.liuxianfa.junit.springboot.entity.User;
import com.liuxianfa.junit.springboot.jsonserializer.BigDecimalSerializer;
import com.liuxianfa.junit.springboot.jsonserializer.StringDateSerializer;
import com.liuxianfa.junit.springboot.request.XssHttpServletRequestWrapper;
import com.liuxianfa.junit.springboot.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 3/17 16:44
 */
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {
        helloService.hello(name);

//        ThreadUtil.sleep(5, TimeUnit.SECONDS);

        return "controller hello " + name;
    }


    @PostMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        FileOutputStream out = new FileOutputStream("d://tmp.file");
        IoUtil.copy(inputStream, out);
        IoUtil.close(out);
        return file.getSize() + "";
    }


    /**
     * 两个 @RequestBody参数
     *
     * <pre>
     * 使用 {@link XssHttpServletRequestWrapper} 可以让request输入流重复读取,
     * 此时就可以写多个 @RequestBody 参数了.
     *
     *
     * curl -X POST --location "http://localhost:8080/moreRequestBodyParam" \
     *     -H "Content-Type: application/json" \
     *     -d "{
     *           \"detailAddress\": \"北京市丰台区xxx大厦\",
     *           \"userName\": \"安小乐\"
     *         }"
     * </pre>
     */
    @RequestMapping("moreRequestBodyParam")
    @ResponseBody
    public ArrayList<Object> moreRequestBodyParam(@RequestBody Address address, @RequestBody User user) {
        System.out.println(JSONUtil.toJsonPrettyStr(address));
        System.out.println(JSONUtil.toJsonPrettyStr(user));
        return CollUtil.newArrayList(address, user);
    }

    /**
     * 此接口参数是List,可以直接进行参数绑定.
     *
     * @param users
     * @return
     */
    @RequestMapping("helloList")
    @ResponseBody
    public List<User> helloList(@RequestBody List<User> users) {
        System.out.println(JSONUtil.toJsonPrettyStr(users));
        return users;
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

        @JsonSerialize(using = StringDateSerializer.NormDateFormatJsonSerializer.class)
        String tradeDateString = "20210503";

        @JsonSerialize(using = StringDateSerializer.ChineseDatePatternJsonSerializer.class)
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
