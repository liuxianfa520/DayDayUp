package com.anxiaole.multitenancy;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:14
 */
@Configurable
@Component
public class RoutingDataSourceConfig {

    @Value("${zk.host:localhost:2181}")
    private String zkHost;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(zkHost);
    }

    @Bean
    public RoutingDataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Collections.emptyMap());
        return routingDataSource;
    }
}