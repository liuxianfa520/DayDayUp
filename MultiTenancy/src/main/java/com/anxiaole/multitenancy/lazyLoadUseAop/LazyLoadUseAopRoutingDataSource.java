package com.anxiaole.multitenancy.lazyLoadUseAop;

import com.anxiaole.multitenancy.listener.TenantAddListener;
import com.anxiaole.multitenancy.listener.TenantDataSourceConfigChangeListener;
import com.anxiaole.multitenancy.listener.TenantDeletedListener;
import com.anxiaole.multitenancy.utils.TenantIdHolder;
import com.anxiaole.multitenancy.utils.Utils;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import static com.anxiaole.multitenancy.utils.Utils.jdbcPrefix;

/**
 * 懒加载租户数据源 —— 在程序使用租户的数据源时,才去初始化此租户的数据源.
 *
 * @author LiuXianfa
 * 
 * @date 4/8 19:05
 */
public class LazyLoadUseAopRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(LazyLoadUseAopRoutingDataSource.class);
    private static final ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    ZkClient zkClient;

    public LazyLoadUseAopRoutingDataSource(ZkClient zkClient) {
        this.zkClient = zkClient;
        init();
    }

    public void init() {
        // 监听子 jdbcPrefix 的节点变化.比如新增了一个租户、删除了一个租户.
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantAddListener(targetDataSources, this, zkClient));
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantDeletedListener(targetDataSources, this));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantIdHolder.getTenantId();
    }

    public DataSource reloadTenantDataSource() {
        TenantDataSourceConfigChangeListener dataListener = new TenantDataSourceConfigChangeListener(zkClient, targetDataSources, this);
        return Utils.reloadTenantDataSource(TenantIdHolder.getTenantId(), zkClient, targetDataSources, this, dataListener);
    }
}
