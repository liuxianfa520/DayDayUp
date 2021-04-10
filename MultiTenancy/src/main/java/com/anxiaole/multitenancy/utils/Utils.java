package com.anxiaole.multitenancy.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;
import com.anxiaole.multitenancy.exception.CreateDataSourceException;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/10 23:34
 */
public class Utils {
    public static final Logger log = LoggerFactory.getLogger(Utils.class);


    /**
     * 数据库连接配置在ZooKeeper中的前缀
     */
    public static final String jdbcPrefix = "/jdbcConfig";
    public static final String jdbcPrefix2 = jdbcPrefix + "/";


    public static String pathToTenantId(String dataPath) {
        return dataPath.replace(jdbcPrefix2, "");
    }

    public static String tenantIdToPath(String tenantId) {
        return jdbcPrefix2 + tenantId;
    }


    public static DataSource reloadTenantDataSource(String tenantId, ZkClient zkClient,
                                                    Map<Object, Object> targetDataSources,
                                                    AbstractRoutingDataSource routingDataSource) {

        return reloadTenantDataSource(tenantId, zkClient, targetDataSources, routingDataSource, null);
    }

    public static DataSource reloadTenantDataSource(String tenantId, ZkClient zkClient,
                                                    Map<Object, Object> targetDataSources,
                                                    AbstractRoutingDataSource routingDataSource,
                                                    IZkDataListener zkDataListener) {
        DataSource dataSource = newTenantDataSource(tenantId, zkClient, zkDataListener);

        targetDataSources.put(tenantId, dataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        // 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        routingDataSource.afterPropertiesSet();
        return dataSource;
    }

    public static DataSource newTenantDataSource(String tenantId, ZkClient zkClient) {
        return newTenantDataSource(tenantId, zkClient, null);
    }

    public static DataSource newTenantDataSource(String tenantId, ZkClient zkClient, IZkDataListener dataChangeListener) {
        String path = tenantIdToPath(tenantId);
        if (dataChangeListener != null) {
            // 监听节点数据变化.
            zkClient.subscribeDataChanges(path, dataChangeListener);
        }
        String jdbcConfig = zkClient.readData(path, true);
        Properties properties = JSON.parseObject(jdbcConfig, Properties.class);
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
            if (dataSource instanceof DruidDataSource) {
                // 获取链接时:失败重试10次.
                ((DruidDataSource) dataSource).setConnectionErrorRetryAttempts(10);
                // 获取链接时:重试次数结束后,跳出循环
                ((DruidDataSource) dataSource).setBreakAfterAcquireFailure(true);
                // 获取数据库链接失败超过重试次数后:快速失败
                ((DruidDataSource) dataSource).setFailFast(true);
            }
        } catch (Exception e) {
            log.error("创建Druid数据源失败.", e);
            throw new CreateDataSourceException(tenantId, e);
        }
        return dataSource;
    }

}
