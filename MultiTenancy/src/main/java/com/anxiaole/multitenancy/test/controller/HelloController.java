package com.anxiaole.multitenancy.test.controller;

import com.anxiaole.multitenancy.TenantIdHolder;
import com.anxiaole.multitenancy.test.dao.UserDao;
import com.anxiaole.multitenancy.test.mock.MockAddTenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/9 23:18
 */
@RestController
public class HelloController {

    @Autowired
    UserDao userDao;

    /**
     * <pre>
     * 1、数据库中执行 \DayDayUp\MultiTenancy\src\main\resources\sql\db.sql
     * 2、本地启动 ZooKeeper  使用默认端口号:2181
     * 3、修改数据库连接账号、密码. {@link MockAddTenant#username} {@link MockAddTenant#password}
     * 4、启动项目
     * 5、调用接口:
     * 调用  http://localhost:8080/hello?tenantId=1 接口,会返回:   (当前租户id:1)   你好:[这是从数据库2中查询出来的名字].
     * 调用  http://localhost:8080/hello?tenantId=2 接口,会返回:   (当前租户id:2)   你好:[这是从数据库2中查询出来的名字].
     * </pre>
     */
    @RequestMapping("hello")
    public String hello() {
        String name = userDao.findFirstUserName();
        return String.format("(当前租户id:%s)   你好:%s.    ", TenantIdHolder.getTenantId(), name);
    }
}
