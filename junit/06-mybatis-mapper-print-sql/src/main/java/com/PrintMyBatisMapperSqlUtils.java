package com;


import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;

import cn.hutool.core.io.FileUtil;

public class PrintMyBatisMapperSqlUtils {

    /**
     * 打印sql语句
     *
     * @param resource          资源地址   如果xml在 resources/mapper/UserMapper.xml则填写  mapper/UserMapper.xml
     * @param mappedStatementId 查询方法id
     * @param param             参数
     */
    public void printSql(String resource, String mappedStatementId, HashMap<String, Object> param) {
        resource = resource.replace("/resources/", "");
        Configuration configuration = new Configuration();
        XMLMapperBuilder builder = new XMLMapperBuilder(FileUtil.getInputStream(resource), configuration, resource, configuration.getSqlFragments());
        builder.parse();

        MappedStatement selectById = configuration.getMappedStatement(mappedStatementId);
        String sql = selectById.getBoundSql(param).getSql();
        System.out.println(String.format("███SQL：%s", sql));
    }
}