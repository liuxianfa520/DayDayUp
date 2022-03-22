package com.anxiaole.multitenancy.test.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;

import lombok.SneakyThrows;

/**
 * @author LiuXianfa
 * 
 * @date 4/10 23:38
 */
@Service
public class UserDao {

    @Autowired
    AbstractRoutingDataSource routingDataSource;

    @SneakyThrows
    public String findFirstUserName() {
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
