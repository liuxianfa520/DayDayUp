package com.anxiaole.multitenancy.test.mock;


import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 使用ZooKeeper作为配置中心
 * 当配置信息有修改的时候,可以通过ZooKeeper的watch机制获得通知,然后重新初始化数据源信息.
 * 理论上来说,需要有一个配置中心web页面项目,在页面上配置完租户的jdbc之后,把数据保存到ZooKeeper中.
 *
 * 模拟:启动时向ZooKeeper中写入租户数据库配置信息
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/9 23:23
 */
@Component
public class MockAddTenant {

    public static String username = "root";
    public static String password = "tiger";

    @Autowired
    ZkClient zkClient;

    private String tenant_1 = "{" +
            "\"url\": \"jdbc:mysql://localhost:3306/test1\"," +
            "\"username\": \"" + username + "\"," +
            "\"password\": \"" + password + "\"," +
            "\"driverclassname\": \"com.mysql.jdbc.Driver\"" +
            "}";

    private String tenant_2 = "{" +
            "\"url\": \"jdbc:mysql://localhost:3306/test2\"," +
            "\"username\": \"" + username + "\"," +
            "\"password\": \"" + password + "\"," +
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
