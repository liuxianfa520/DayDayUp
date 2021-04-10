package com.anxiaole.multitenancy.initAllOnStartup;

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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static com.anxiaole.multitenancy.utils.Utils.jdbcPrefix;

/**
 * 在启动时一次性把所有租户的数据源都初始化完毕 的多租户切换数据源
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:05
 */
public class InitAllOnStartupRoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(InitAllOnStartupRoutingDataSource.class);
    private static final ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    @Autowired
    ZkClient zkClient;

    @PostConstruct
    public void init() {
        initAllTenantDataSource();

        // 监听子 jdbcPrefix 的节点变化.比如新增了一个租户、删除了一个租户.
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantAddListener(targetDataSources, this, zkClient));
        zkClient.subscribeChildChanges(jdbcPrefix, new TenantDeletedListener(targetDataSources, this));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantIdHolder.getTenantId();
    }

    private void initAllTenantDataSource() {
        List<String> children = getChildrenOrEmptyList();
        for (String tenantId : children) {
            DataSource dataSource = Utils.newTenantDataSource(tenantId, zkClient, new TenantDataSourceConfigChangeListener(zkClient, targetDataSources, this));
            targetDataSources.put(tenantId, dataSource);
        }
        this.setTargetDataSources(targetDataSources);
        // 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        this.afterPropertiesSet();
    }

    private List<String> getChildrenOrEmptyList() {
        try {
            return zkClient.getChildren(jdbcPrefix);
        } catch (org.I0Itec.zkclient.exception.ZkNoNodeException e) {
            log.warn("不存在租户数据库配置信息", e);
            return Collections.emptyList();
        }
    }

}
