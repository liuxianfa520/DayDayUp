package com.pandora.storage.es.entity;

import java.util.Map;

import lombok.Data;

/**
 * Pointer数据结构为：
 * <pre>
 * {
 *     "__type":"Pointer",
 *     "className":"author",
 *     "objectId":"5e1e83db0e0f2e2c1f0964f1"
 * }
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/15 11:10
 */
@Data
public class Pointer {

    public static final String TYPE_KEY = "__type";

    public static final String __type = "Pointer";

    private String className;
    private String objectId;

    public String get___type() {
        return __type;
    }

    /**
     * 判断一个对象是否是Pointer
     */
    public static boolean typeIsPointer(Object object) {
        return object instanceof Map && __type.equals(((Map) object).get("__type"));
    }
}