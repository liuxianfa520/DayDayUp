package com.anxiaole.multitenancy.listener;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

import static com.anxiaole.multitenancy.utils.Utils.reloadTenantDataSource;
import static com.anxiaole.multitenancy.utils.Utils.pathToTenantId;

/**
 * @author LiuXianfa
 * 
 * @date 4/11 0:47
 */
public class TenantDataSourceConfigChangeListener implements org.I0Itec.zkclient.IZkDataListener {

    private ZkClient zkClient;
    private Map<Object, Object> targetDataSources;
    private AbstractRoutingDataSource routingDataSource;


    public TenantDataSourceConfigChangeListener(ZkClient zkClient, Map<Object, Object> targetDataSources, AbstractRoutingDataSource routingDataSource) {
        this.zkClient = zkClient;
        this.targetDataSources = targetDataSources;
        this.routingDataSource = routingDataSource;
    }

    @Override
    public void handleDataChange(String dataPath, Object data) {
        String tenantId = pathToTenantId(dataPath);

        reloadTenantDataSource(tenantId, zkClient, targetDataSources, routingDataSource);
    }

    @Override
    public void handleDataDeleted(String dataPath) {
        zkClient.unsubscribeDataChanges(dataPath, null);// 移除所有此节点的监听

        String tenantId = pathToTenantId(dataPath);
        targetDataSources.remove(tenantId);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
    }
}