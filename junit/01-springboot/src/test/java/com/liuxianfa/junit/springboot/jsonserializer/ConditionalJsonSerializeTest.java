package com.liuxianfa.junit.springboot.jsonserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxianfa.junit.springboot.SpringbootApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @date 2022/4/21 17:39
 */
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class ConditionalJsonSerializeTest {

    @Data
    @AllArgsConstructor
    public static class User {
        String name;
        @ConditionalJsonSerialize(condition = "'${spring.profiles.active}' == 'dev'")
        String password;
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void name() throws JsonProcessingException {
        User user = new User("安小乐", "123456");
        System.out.println(objectMapper.writeValueAsString(user));
    }


    @Data
    @AllArgsConstructor
    public static class User2 {
        String name;
        // 表达式中,可以使用当前对象中的字段,来作为判断条件
        @ConditionalJsonSerialize(condition = "name.equals('admin')")
        String hobby;
    }

    @Test
    public void test2() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(new User2("admin", "吃饭,睡觉,打豆豆")));
        System.out.println(objectMapper.writeValueAsString(new User2("豆豆", "吃饭,睡觉")));
    }

    @Data
    @AllArgsConstructor
    public static class User3 {
        String name;
        // 表达式中,可以使用当前对象中的方法,来作为判断条件
        @ConditionalJsonSerialize(condition = "hello().equals('Hello,admin')")
        String hobby;

        public String hello() {
            return String.format("Hello,%s", name);
        }
    }

    @Test
    public void test3() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(new User3("admin", "吃饭,睡觉,打豆豆")));
        System.out.println(objectMapper.writeValueAsString(new User3("豆豆", "吃饭,睡觉")));
    }
}
