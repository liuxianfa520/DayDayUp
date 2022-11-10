package com.liuxianfa.junit.springboot.datasources;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.liuxianfa.junit.springboot.datasources.annotation.DataSourceNames;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * 配置多数据源
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConditionalOnProperty(value = "spring.datasource.druid.user.url")
    @ConfigurationProperties("spring.datasource.druid.user")
    public DataSource userDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.datasource.druid.order.url")
    @ConfigurationProperties("spring.datasource.druid.order")
    public DataSource orderDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource userDataSource, DataSource orderDataSource) {
        Map<String, DataSource> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.USER, userDataSource);
        targetDataSources.put(DataSourceNames.ORDER, orderDataSource);
        return new DynamicDataSource(userDataSource, targetDataSources);
    }
}
