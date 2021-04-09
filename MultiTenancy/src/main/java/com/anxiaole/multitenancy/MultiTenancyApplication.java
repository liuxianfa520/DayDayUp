package com.anxiaole.multitenancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:19
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MultiTenancyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenancyApplication.class, args);
    }
}
