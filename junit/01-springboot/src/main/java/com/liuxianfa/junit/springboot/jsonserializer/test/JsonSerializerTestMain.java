package com.liuxianfa.junit.springboot.jsonserializer.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxianfa.junit.springboot.jsonserializer.JsonEnumSerialize;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
class UserEntity {
    String name;

    @JsonEnumSerialize(enumClass = UserStatusEnums.class)
    String status;

    @JsonEnumSerialize(enumClass = UserStatusEnums.class, fieldName = "userStatusDesc2")
    String status2;
}

public class JsonSerializerTestMain {
    public static void main(String[] args) throws JsonProcessingException {
        UserEntity user = new UserEntity()
                .setName("张三")
                .setStatus(UserStatusEnums.ENABLE.getCode())
                .setStatus2(UserStatusEnums.DISABLE.getCode());

        // 使用 hutool 和 fastjson 的序列化,不行
        System.out.println(JSON.toJSONString(user));
        System.out.println(JSONUtil.toJsonStr(user));

        // 需要使用 ObjectMapper 的序列化
        System.out.println(new ObjectMapper().writeValueAsString(user));
    }
}

