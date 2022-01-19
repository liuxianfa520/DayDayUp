package com.liuxianfa.junit.springboot.datasources;

import com.zaxxer.hikari.HikariDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 配置多数据源
 *
 * @author xianfaliu2@creditease.cn
 */
@Configuration
@MapperScan(basePackages = "com.liuxianfa.junit.springboot.order.dao", sqlSessionFactoryRef = "orderSqlSessionFactory")
public class OrderDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari.order")
    public DataSource orderDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "orderSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/order/*.xml"));
        sessionFactory.setDataSource(orderDataSource());
        // mybatis的配置:驼峰式映射成下划线
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sessionFactory.getObject();
    }

    @Bean(name = "orderTransactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.orderDataSource());
    }

    @Bean("orderSqlSessionTemplate")
    public SqlSessionTemplate tradingSqlSessionTemplate(SqlSessionFactory orderSqlSessionFactory) {
        return new SqlSessionTemplate(orderSqlSessionFactory);
    }
}
