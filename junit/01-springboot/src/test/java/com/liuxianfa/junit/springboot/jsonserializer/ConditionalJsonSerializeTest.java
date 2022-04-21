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
}
