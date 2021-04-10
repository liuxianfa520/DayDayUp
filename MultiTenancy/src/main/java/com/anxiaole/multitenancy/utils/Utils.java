package com.anxiaole.multitenancy.utils;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/10 23:34
 */
public class Utils {
    private Utils() {

    }


    /**
     * 数据库连接配置在ZooKeeper中的前缀
     */
    public static final String jdbcPrefix = "/jdbcConfig";
    public static final String jdbcPrefix2 = jdbcPrefix + "/";


    public static String pathToTenantId(String dataPath) {
        return dataPath.replace(jdbcPrefix2, "");
    }

    public static String tenantIdToPath(String tenantId) {
        return jdbcPrefix2 + tenantId;
    }

}
