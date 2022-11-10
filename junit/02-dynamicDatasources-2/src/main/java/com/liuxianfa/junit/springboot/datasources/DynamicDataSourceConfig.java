package com.liuxianfa.junit.springboot.datasources;


import com.liuxianfa.junit.springboot.datasources.annotation.DataSourceNames;
import com.zaxxer.hikari.HikariDataSource;

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
    @ConditionalOnProperty(value = "spring.datasource.hikari.user.jdbcUrl")
    @ConfigurationProperties("spring.datasource.hikari.user")
    public DataSource userDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.datasource.hikari.order.jdbcurl")
    @ConfigurationProperties("spring.datasource.hikari.order")
    public DataSource orderDataSource() {
        return new HikariDataSource();
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
