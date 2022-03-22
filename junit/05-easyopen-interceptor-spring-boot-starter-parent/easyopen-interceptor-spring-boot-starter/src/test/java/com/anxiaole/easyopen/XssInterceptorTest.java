package com.anxiaole.easyopen;

import com.anxiaole.easyopen.interceptors.xss.XssInterceptor;

import com.alibaba.fastjson.JSON;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 11/25 21:08
 */
public class XssInterceptorTest {

    private String xssString = "\"><script>alert('XSS');</script>";

    @Data
    class UserEntity {
        private String name;
        private EmpEntity empEntity;
        private List<String> list;
        private List<EmpEntity> empEntities;

        private Set<String> set;
        private Set<EmpEntity> empEntitieSet;
    }

    @Data
    class EmpEntity {
        private String mobile;
    }

    @Test
    void name() throws Exception {
        XssInterceptor xssInterceptor = new XssInterceptor();
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            put("name", xssString);
        }};
        xssInterceptor.preHandle(null, null, null, map);
        System.out.println(JSON.toJSONString(map, true));


        // 测试com.anxiaole.*.Xxx类型方法参数中包含String类型字段
        UserEntity userEntity = new UserEntity();
        userEntity.setName(xssString);
        xssInterceptor.preHandle(null, null, null, userEntity);
        System.out.println(JSON.toJSONString(userEntity, true));


        // 测试com.anxiaole.*.Xxx类型方法参数中包含com.anxiaole.*.Xxx类型字段
        userEntity.setEmpEntity(new EmpEntity() {{
            setMobile(xssString);
        }});
        xssInterceptor.preHandle(null, null, null, userEntity);
        System.out.println(JSON.toJSONString(userEntity, true));


        // 测试com.anxiaole.*.Xxx类型方法参数中包含List<String>/Set<String>类型字段
        userEntity.setList(Collections.singletonList(xssString));
        userEntity.setSet(Collections.singleton(xssString));
        xssInterceptor.preHandle(null, null, null, userEntity);
        System.out.println(JSON.toJSONString(userEntity, true));


        // 测试com.anxiaole.*.Xxx类型方法参数中包含List<com.anxiaole.*.Xxx>、 Set<com.anxiaole.*.Xxx>类型字段
        userEntity.setEmpEntities(Collections.singletonList(new EmpEntity() {{
            setMobile(xssString);
        }}));
        userEntity.setEmpEntitieSet(Collections.singleton(new EmpEntity() {{
            setMobile(xssString);
        }}));
        xssInterceptor.preHandle(null, null, null, userEntity);
        System.out.println(JSON.toJSONString(userEntity, true));

    }
}
