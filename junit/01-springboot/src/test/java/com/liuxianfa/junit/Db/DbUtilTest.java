package com.liuxianfa.junit.Db;

import java.sql.SQLException;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;

/**
 * https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E5%BA%93%E7%AE%80%E5%8D%95%E6%93%8D%E4%BD%9C-Db?id=%e4%bd%bf%e7%94%a8
 *
 * @author xianfaliu
 * @date 2022/4/2 15:58
 */
public class DbUtilTest {

    public static void main(String[] args) throws SQLException {
        crud();
    }

    private static void crud() throws SQLException {

        // sql
        System.out.println(Db.use().query("select * from t_user"));


        Db.use().insert(
                Entity.create("t_user")
                      .set("name", "unitTestUser")
                      .set("age", 66)
        );


        // 插入数据并返回自增主键：
        Long id = Db.use().insertForGeneratedKey(
                Entity.create("t_user")
                      .set("name", "unitTestUser")
                      .set("age", 66)
        );

        Db.use().update(
                Entity.create().set("age", 88), //修改的数据
                Entity.create("t_user").set("name", "unitTestUser") //where条件
        );


        Db.use().del(
                Entity.create("t_user").set("name", "unitTestUser")//where条件
        );


        //t_user为表名
        System.out.println(JSONUtil.toJsonPrettyStr(Db.use().findAll("t_user")));
    }
}
