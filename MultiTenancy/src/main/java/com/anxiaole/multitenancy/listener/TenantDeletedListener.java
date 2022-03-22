package com.anxiaole.multitenancy.listener;

import org.I0Itec.zkclient.IZkChildListener;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户移除 监听器
 *
 * @author LiuXianfa
 * 
 * @date 4/11 0:55
 */
public class TenantDeletedListener implements IZkChildListener {

    private ConcurrentHashMap<Object, Object> targetDataSources;
    private AbstractRoutingDataSource routingDataSource;

    public TenantDeletedListener(ConcurrentHashMap<Object, Object> targetDataSources, AbstractRoutingDataSource routingDataSource) {
        this.targetDataSources = targetDataSources;
        this.routingDataSource = routingDataSource;
    }

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        // 1、如果是删除节点:
        ConcurrentHashMap.KeySetView<Object, Object> deletedTenant = targetDataSources.keySet();
        deletedTenant.removeIf(currentChilds::contains);
        if (!CollectionUtils.isEmpty(deletedTenant)) {// 已删除的租户id
            for (Object tenantId : deletedTenant) {
                targetDataSources.remove(tenantId);
            }
        }

        // 2、重新初始化数据源
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
    }
}
