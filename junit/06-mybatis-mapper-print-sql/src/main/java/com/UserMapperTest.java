package com;

import java.util.HashMap;

public class UserMapperTest extends PrintMyBatisMapperSqlUtils {

    public static void main(String[] args) {
        UserMapperTest userMapperTest = new UserMapperTest();
        HashMap<String, Object> param = new HashMap<>();
        param.put("name", "zhangsan");
        param.put("age", 10);
        param.put("address", "北京");
        userMapperTest.printSql("UserMapper.xml", "selectUser", param);
    }
}
