package com.pandora.storage.es.api;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/27 15:39
 */
public class IndexNameUtils {

    public static String indexName(String appId, String className) {
        return (appId + "_" + className).toLowerCase();
    }
}
