package com.anxiaole.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/8 19:05
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    public static final ThreadLocal<String> tenantIdHolder = new ThreadLocal();

    @Override
    protected Object determineCurrentLookupKey() {
        return getTenantId();
    }

    public static void setTenantId(String tenantId) {
        tenantIdHolder.set(tenantId);
    }

    public static String getTenantId() {
        return tenantIdHolder.get();
    }

    public static void clear() {
        tenantIdHolder.remove();
    }

}
