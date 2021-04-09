package com.anxiaole.multitenancy.controller;

import com.anxiaole.multitenancy.RoutingDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;

import lombok.SneakyThrows;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/9 23:18
 */
@RestController
public class HelloController {

    public static String username = "root";
    public static String password = "tiger";

    /**
     * <pre>
     * 1、数据库中执行 \DayDayUp\MultiTenancy\src\main\resources\sql\db.sql
     * 2、本地启动 ZooKeeper  使用默认端口号:2181
     * 3、修改上面数据库连接账号、密码.
     * 4、启动项目
     * 5、调用接口:
     * 调用  http://localhost:8080/hello?tenantId=1 接口,会返回:   (当前租户id:1)   你好:[这是从数据库2中查询出来的名字].
     * 调用  http://localhost:8080/hello?tenantId=2 接口,会返回:   (当前租户id:2)   你好:[这是从数据库2中查询出来的名字].
     * </pre>
     */
    @RequestMapping("hello")
    public String hello() {
        String name = findFirst();
        return String.format("(当前租户id:%s)   你好:%s.    ", RoutingDataSource.getTenantId(), name);
    }

    @Autowired
    RoutingDataSource routingDataSource;

    @SneakyThrows
    private String findFirst() {
        String sql = "select name from user limit 1";

        try (ResultSet rs = routingDataSource.getConnection().prepareStatement(sql).executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null) {
                    return name;
                }
            }
        }
        throw new RuntimeException("查询数据库失败!");
    }
}
