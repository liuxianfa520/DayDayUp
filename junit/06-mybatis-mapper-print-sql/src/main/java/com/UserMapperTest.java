package com;

import java.util.HashMap;

public class UserMapperTest extends PrintMyBatisMapperSqlUtils {

    public static void main(String[] args) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("name", "zhangsan");
        param.put("age", 10);
        param.put("address", "北京");
        param.put("xxx", "1");
        param.put("bbb", "b");

        printSql("UserMapper.xml", "selectUser", param);
    }
}
