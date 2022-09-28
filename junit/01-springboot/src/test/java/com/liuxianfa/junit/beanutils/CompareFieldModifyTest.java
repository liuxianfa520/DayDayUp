package com.liuxianfa.junit.beanutils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 对比两个对象中,哪些字段被修改了
 */
public class CompareFieldModifyTest {

    @Data
    @Accessors(chain = true)
    public static class User {
        Integer id;
        String name;
        String age;
        String address;
        List<String> hobby;
    }

    public static void main(String[] args) {
        User db = new User().setId(1).setName("张三").setAge("13").setAddress("北京").setHobby(Arrays.asList("篮球", "游戏"));
        User dto = new User().setName("张三").setAge("14").setAddress("北京朝阳区").setHobby(Arrays.asList("篮球", "游戏", "编程"));

        User user = compareFieldValue(db, dto);
        System.out.println(JSONUtil.toJsonPrettyStr(user));
    }

    /**
     * @param db  数据库中的数据
     * @param dto 前端修改后的数据
     * @return 对比前端修改了哪些字段及字段值
     */
    private static User compareFieldValue(User db, User dto) {
        User 修改的字段 = new User();
        Field[] fields = ReflectUtil.getFields(db.getClass());
        for (Field field : fields) {
            Object fieldValue = BeanUtil.getFieldValue(db, field.getName());
            Object fieldValue2 = BeanUtil.getFieldValue(dto, field.getName());
            if (!Objects.equals(fieldValue, fieldValue2)) {
                BeanUtil.setFieldValue(修改的字段, field.getName(), fieldValue2);
            }
            if (fieldValue instanceof Collection || fieldValue2 instanceof Collection) {
                if (!CollUtil.isEqualList((Collection) fieldValue, (Collection) fieldValue2)) {
                    BeanUtil.setFieldValue(修改的字段, field.getName(), fieldValue2);
                }
            }
        }
        return 修改的字段;
    }
}