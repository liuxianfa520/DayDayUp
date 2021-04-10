package com.anxiaole.multitenancy;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/10 22:16
 */
public class TenantIdHolder {
    private TenantIdHolder() {
    }

    private static final ThreadLocal<String> tenantIdHolder = new ThreadLocal();

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
