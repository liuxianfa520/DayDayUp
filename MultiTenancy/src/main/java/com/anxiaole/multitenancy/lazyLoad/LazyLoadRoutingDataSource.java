package com.anxiaole.multitenancy.lazyLoad;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;
import com.anxiaole.multitenancy.TenantIdHolder;
import com.anxiaole.multitenancy.exception.CreateDataSourceException;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * 懒加载租户数据源 —— 在程序使用租户的数据源时,才去初始化此租户的数据源.
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:05
 */
public class LazyLoadRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(LazyLoadRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantIdHolder.getTenantId();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        DataSource dataSource;
        try {
            // 懒加载租户数据源:程序第一次使用tenantId=1的数据源时会报错 IllegalStateException.
            // 报错时,去初始化租户的数据源并且重新初始化 RoutingDataSource
            dataSource = super.determineTargetDataSource();
        } catch (IllegalStateException e) {
            log.warn(String.format("当前租户的数据源尚未初始化.现在去初始化.tenantId=[%s].", TenantIdHolder.getTenantId()), e);
            dataSource = initTenantDataSource(TenantIdHolder.getTenantId(), new ZkDataListener());
            if (dataSource == null) {
                throw new CreateDataSourceException(TenantIdHolder.getTenantId());
            }
        }
        return dataSource;
    }

    @Autowired
    ZkClient zkClient;

    private static final ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    /**
     * 数据库连接配置在ZooKeeper中的前缀
     */
    private static final String jdbcPrefix = "/jdbcConfig";
    private static final String jdbcPrefix2 = jdbcPrefix + "/";


    class ZkDataListener implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) {
            String tenantId = pathToTenantId(dataPath);
            initTenantDataSource(tenantId);
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            zkClient.unsubscribeDataChanges(dataPath, null);// 移除所有此节点的监听

            String tenantId = pathToTenantId(dataPath);
            targetDataSources.remove(tenantId);
            LazyLoadRoutingDataSource.this.setTargetDataSources(targetDataSources);
            LazyLoadRoutingDataSource.this.afterPropertiesSet();
        }
    }

    @PostConstruct
    public void init() {
        // 监听子 jdbcPrefix 的节点变化.比如新增了一个租户、删除了一个租户.
        zkClient.subscribeChildChanges(jdbcPrefix, (parentPath, currentChilds) -> {
            // 1、如果是删除节点:
            ConcurrentHashMap.KeySetView<Object, Object> deletedTenant = targetDataSources.keySet();
            deletedTenant.removeIf(currentChilds::contains);
            if (!CollectionUtils.isEmpty(deletedTenant)) {// 已删除的租户id
                for (Object tenantId : deletedTenant) {
                    targetDataSources.remove(tenantId);
                }
            }

            // 2、重新初始化数据源
            this.setTargetDataSources(targetDataSources);
            this.afterPropertiesSet();
        });
    }

    private String pathToTenantId(String dataPath) {
        return dataPath.replace(jdbcPrefix2, "");
    }

    private DataSource initTenantDataSource(String tenantId) {
        return initTenantDataSource(tenantId, null);
    }


    private DataSource initTenantDataSource(String tenantId, IZkDataListener dataChangeListener) {
        DataSource dataSource = createDataSource(tenantId, zkClient, dataChangeListener);
        targetDataSources.put(tenantId, dataSource);
        this.setTargetDataSources(targetDataSources);
        // 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        this.afterPropertiesSet();
        return dataSource;
    }

    private DataSource createDataSource(String tenantId, ZkClient zkClient) {
        return createDataSource(tenantId, zkClient, null);
    }

    private DataSource createDataSource(String tenantId, ZkClient zkClient, IZkDataListener dataChangeListener) {
        String path = jdbcPrefix2 + tenantId;
        if (dataChangeListener != null) {
            // 监听节点数据变化.
            zkClient.subscribeDataChanges(path, dataChangeListener);
        }
        String jdbcConfig = zkClient.readData(path, true);
        Properties properties = JSON.parseObject(jdbcConfig, Properties.class);
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            log.error("创建Druid数据源失败.", e);
            throw new CreateDataSourceException(tenantId, e);
        }
        return dataSource;
    }
}
