package com.anxiaole.multitenancy.listener;

import com.anxiaole.multitenancy.utils.Utils;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

/**
 * 新租户 监听器
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/11 1:11
 */
public class TenantAddListener implements IZkChildListener {

    private ConcurrentHashMap<Object, Object> targetDataSources;
    private AbstractRoutingDataSource routingDataSource;
    private ZkClient zkClient;

    public TenantAddListener(ConcurrentHashMap<Object, Object> targetDataSources, AbstractRoutingDataSource routingDataSource, ZkClient zkClient) {
        this.targetDataSources = targetDataSources;
        this.routingDataSource = routingDataSource;
        this.zkClient = zkClient;
    }

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        for (String tenantId : currentChilds) {
            if (!targetDataSources.contains(tenantId)) {
                // 1、说明是新增的租户
                TenantDataSourceConfigChangeListener tenantDataSourceConfigChangeListener = new TenantDataSourceConfigChangeListener(zkClient, targetDataSources, routingDataSource);
                DataSource hikariDataSource = Utils.newTenantDataSource(tenantId, zkClient, tenantDataSourceConfigChangeListener); // 新增的租户需要添加节点监听.
                targetDataSources.put(tenantId, hikariDataSource);
            }
        }
    }
}
