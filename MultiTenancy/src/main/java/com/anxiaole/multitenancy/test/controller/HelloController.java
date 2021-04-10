package com.anxiaole.multitenancy.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anxiaole.multitenancy.test.dao.UserDao;
import com.anxiaole.multitenancy.test.mock.MockAddTenant;
import com.anxiaole.multitenancy.utils.TenantIdHolder;
import com.anxiaole.multitenancy.utils.Utils;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;

import lombok.SneakyThrows;

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


    @Autowired
    ZkClient zkClient;

    /**
     * <pre>
     * 修改租户数据源配置.
     * 可以把数据源配置修改错误,然后在调用上面的接口试一下.
     * 主要是为了测试 配置修改后,数据源会不会重新初始化.
     *
     * 注意:还需要在请求头或者请求参数中传入tenantId
     * </pre>
     *
     * @param username
     * @param password
     * @param url
     * @param driverclassname
     * @return
     */
    @SneakyThrows
    @RequestMapping("addOrUpdateTenantDataSource")
    public JSONObject changeJdbc(String username, String password, String url, String driverclassname) {
        String path = Utils.tenantIdToPath(TenantIdHolder.getTenantId());
        String jdbc = zkClient.readData(path, true);
        if (jdbc == null) {
            jdbc = "{}";
        }
        JSONObject jsonObject = JSON.parseObject(jdbc);
        if (StringUtils.hasText(username)) {
            jsonObject.put("username", username);
        }
        if (StringUtils.hasText(password)) {
            jsonObject.put("password", password);
        }
        if (StringUtils.hasText(url)) {
            jsonObject.put("url", URLDecoder.decode(url, "UTF-8"));
        }
        if (StringUtils.hasText(driverclassname)) {
            jsonObject.put("driverclassname", driverclassname);
        }
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }
        zkClient.writeData(path, JSON.toJSONString(jsonObject));
        return JSON.parseObject(zkClient.readData(path));
    }

    @RequestMapping("deleteTenant")
    public String deleteTenant() {
        String path = Utils.tenantIdToPath(TenantIdHolder.getTenantId());
        zkClient.delete(path);
        return "删除成功";
    }
}
