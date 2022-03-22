package com.anxiaole.multitenancy.lazyLoad;

import com.anxiaole.multitenancy.exception.CreateDataSourceException;
import com.anxiaole.multitenancy.listener.TenantAddListener;
import com.anxiaole.multitenancy.listener.TenantDataSourceConfigChangeListener;
import com.anxiaole.multitenancy.listener.TenantDeletedListener;
import com.anxiaole.multitenancy.utils.TenantIdHolder;
import com.anxiaole.multitenancy.utils.Utils;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static com.anxiaole.multitenancy.utils.Utils.jdbcPrefix;

/**
 * 懒加载租户数据源 —— 在程序使用租户的数据源时,才去初始化此租户的数据源.
 *
 * @author LiuXianfa
 * 
 * @date 4/8 19:05
 */
public class LazyLoadRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(LazyLoadRoutingDataSource.class);
    private static final ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    @Autowired
    ZkClient zkClient;

    @PostConstruct
    public void init() {
        // 监听子 jdbcPrefix 的节点变化.比如新增了一个租户、删除了一个租户.
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantAddListener(targetDataSources, this, zkClient));
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantDeletedListener(targetDataSources, this));
    }

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
            TenantDataSourceConfigChangeListener dataListener = new TenantDataSourceConfigChangeListener(zkClient, targetDataSources, this);
            dataSource = Utils.reloadTenantDataSource(TenantIdHolder.getTenantId(), zkClient, targetDataSources, this, dataListener);
            if (dataSource == null) {
                throw new CreateDataSourceException(TenantIdHolder.getTenantId());
            }
        }
        return dataSource;
    }
}
