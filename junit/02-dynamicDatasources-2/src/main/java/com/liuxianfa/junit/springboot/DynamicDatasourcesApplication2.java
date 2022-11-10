package com.liuxianfa.junit.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan("com.liuxianfa.junit.springboot.*.dao")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DynamicDatasourcesApplication2 {
    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourcesApplication2.class, args);
    }
}
