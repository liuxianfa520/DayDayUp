package com.liuxianfa.junit.springboot.jsonserializer.test;

import lombok.Getter;

/**
 * 用户状态枚举
 */
public enum UserStatusEnums {

    ENABLE("1", "启用"),
    DISABLE("2", "禁用"),
    ACTIVE_LOGOUT("3", "主动注销"),
    ;

    UserStatusEnums(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    @Getter
    public String code;
    @Getter
    public String remark;

    public static UserStatusEnums getByCode(String code) {
        UserStatusEnums[] values = UserStatusEnums.values();
        for (UserStatusEnums object : values) {
            if (object.code.equals(code)) {
                return object;
            }
        }
        return null;
    }
}