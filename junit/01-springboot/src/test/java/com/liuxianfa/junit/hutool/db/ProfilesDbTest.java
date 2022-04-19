package com.liuxianfa.junit.hutool.db;

import com.liuxianfa.junit.springboot.SpringbootApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/4/19 10:02
 */
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class ProfilesDbTest {


    /**
     * src/test/resources/application.yml   修改使用的环境
     * <p>
     * src/test/resources/db.setting      配置多环境的db连接
     */
    @Test
    public void name() throws SQLException {
        String sql = "select * from t_user";

        System.out.println(JSONUtil.toJsonPrettyStr(Db.use().query(sql)));

        System.out.println(JSONUtil.toJsonPrettyStr(Db.use(SpringUtil.getActiveProfile()).query(sql)));
    }
}
