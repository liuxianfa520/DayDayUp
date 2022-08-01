package com.liuxianfa.junit.springboot.typereference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxianfa.junit.springboot.entity.User;

import org.apache.poi.ss.formula.functions.T;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

/**
 * json转java实体时,如果存在泛型,需要使用 {@link TypeReference}
 */
public class TypeReferenceTest {


    static String json =
            "{\n" +
                    "    \"code\": 0,\n" +
                    "    \"data\": {\n" +
                    "        \"name\": \"安小乐\",\n" +
                    "        \"id\": 1\n" +
                    "    }\n" +
                    "}";


    @SneakyThrows
    public static void main(String[] args) {
        Class<User> dataClass = User.class;
        TypeReference<Result<T>> typeReferenceType = new TypeReference<Result<T>>() {
            @Override
            public Type getType() {
                return ParameterizedTypeImpl.make(Result.class, new Type[] {dataClass}, null);
            }
        };

        Result<T> tResult = new ObjectMapper().readValue(json, typeReferenceType);
        System.out.println(JSONUtil.toJsonPrettyStr(tResult));
    }

}