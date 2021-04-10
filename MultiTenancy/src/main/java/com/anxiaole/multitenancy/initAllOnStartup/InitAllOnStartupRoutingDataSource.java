package com.anxiaole.multitenancy.initAllOnStartup;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import lombok.extern.log4j.Log4j;

import static com.anxiaole.multitenancy.TenantIdHolder.getTenantId;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:05
 */
@Log4j
public class InitAllOnStartupRoutingDataSource extends AbstractRoutingDataSource {


    @Override
    protected Object determineCurrentLookupKey() {
        return getTenantId();
    }


    @Autowired
    ZkClient zkClient;

    private static final ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    /**
     * 数据库连接配置在ZooKeeper中的前缀
     */
    private static final String jdbcPrefix = "/jdbcConfig";
    private static final String jdbcPrefix2 = jdbcPrefix + "/";


    class ZkDataListener implements org.I0Itec.zkclient.IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) {
            String tenantId = pathToTenantId(dataPath);
            changeRoutingDataSource(tenantId, zkClient);
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            zkClient.unsubscribeDataChanges(dataPath, null);// 移除所有此节点的监听

            String tenantId = pathToTenantId(dataPath);
            targetDataSources.remove(tenantId);
            InitAllOnStartupRoutingDataSource.this.setTargetDataSources(targetDataSources);
            InitAllOnStartupRoutingDataSource.this.afterPropertiesSet();
        }
    }

    @PostConstruct
    public void init() {
        // note:弊端:需要启动时一次性把所有租户的数据源都初始化完毕.
        //  而无法做到:当需要用到某个租户的数据源时才去初始化.

        List<String> children = getChildrenOrEmptyList();
        for (String tenantId : children) {
            DataSource dataSource = buildDataSource(tenantId, zkClient, new ZkDataListener());
            targetDataSources.put(tenantId, dataSource);
        }
        this.setTargetDataSources(targetDataSources);
        // 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        this.afterPropertiesSet();


        // 监听子 jdbcPrefix 的节点变化.比如新增了一个租户、删除了一个租户.
        zkClient.subscribeChildChanges(jdbcPrefix, (parentPath, currentChilds) -> {
            for (String tenantId : currentChilds) {
                if (!targetDataSources.contains(tenantId)) {
                    // 1、说明是新增的租户
                    DataSource hikariDataSource = buildDataSource(tenantId, zkClient, new ZkDataListener()); // 新增的租户需要添加节点监听.
                    targetDataSources.put(tenantId, hikariDataSource);
                }
            }

            // 2、如果是删除节点:
            ConcurrentHashMap.KeySetView<Object, Object> deletedTenant = targetDataSources.keySet();
            deletedTenant.removeIf(currentChilds::contains);
            if (!CollectionUtils.isEmpty(deletedTenant)) {// 已删除的租户id
                for (Object tenantId : deletedTenant) {
                    targetDataSources.remove(tenantId);
                }
            }

            // 3、重新初始化数据源
            this.setTargetDataSources(targetDataSources);
            this.afterPropertiesSet();
        });
    }


    private List<String> getChildrenOrEmptyList() {
        try {
            return zkClient.getChildren(jdbcPrefix);
        } catch (org.I0Itec.zkclient.exception.ZkNoNodeException e) {
            log.warn("不存在租户数据库配置信息", e);
            return Collections.emptyList();
        }
    }

    private String pathToTenantId(String dataPath) {
        return dataPath.replace(jdbcPrefix2, "");
    }

    private void changeRoutingDataSource(String tenantId, ZkClient zkClient) {
        DataSource dataSource = buildDataSource(tenantId, zkClient);
        targetDataSources.put(tenantId, dataSource);
        this.setTargetDataSources(targetDataSources);
        // 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        this.afterPropertiesSet();
    }

    private DataSource buildDataSource(String tenantId, ZkClient zkClient) {
        return buildDataSource(tenantId, zkClient, null);
    }

    private DataSource buildDataSource(String tenantId, ZkClient zkClient, IZkDataListener dataChangeListener) {
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
        }
        return dataSource;
    }
}
