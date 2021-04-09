package com.anxiaole.multitenancy.mock;

import com.anxiaole.multitenancy.controller.HelloController;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 项目启动时模拟向ZooKeeper中写出租户数据库配置信息
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/9 23:23
 */
@Component
public class MockAddTenant {


    @Autowired
    ZkClient zkClient;

    private String tenant_1 = "{" +
            "\"url\": \"jdbc:mysql://localhost:3306/test1\"," +
            "\"username\": \"" + HelloController.username + "\"," +
            "\"password\": \"" + HelloController.password + "\"," +
            "\"driverclassname\": \"com.mysql.jdbc.Driver\"" +
            "}";

    private String tenant_2 = "{" +
            "\"url\": \"jdbc:mysql://localhost:3306/test2\"," +
            "\"username\": \"" + HelloController.username + "\"," +
            "\"password\": \"" + HelloController.password + "\"," +
            "\"driverclassname\": \"com.mysql.jdbc.Driver\"" +
            "}";

    @PostConstruct
    public void mockAddTenant() {
        saveConfig("1", tenant_1);
        saveConfig("2", tenant_2);
    }

    private static final String jdbcPrefix = "/jdbcConfig";

    private void saveConfig(String tenantId, String config) {
        if (!zkClient.exists(jdbcPrefix)) {
            zkClient.createPersistent(jdbcPrefix);
        }
        String path = jdbcPrefix + "/" + tenantId;
        if (zkClient.exists(path)) {
            zkClient.writeData(path, config);
        } else {
            zkClient.createPersistent(path, config);
        }
    }
}
