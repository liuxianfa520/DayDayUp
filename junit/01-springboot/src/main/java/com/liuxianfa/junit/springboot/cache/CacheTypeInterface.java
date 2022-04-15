package com.liuxianfa.junit.springboot.cache;


import org.springframework.cache.annotation.Cacheable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import static java.util.stream.Collectors.toList;

/**
 * 缓存类型及过期时间
 *
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/17 18:43
 */
public interface CacheTypeInterface {

    /**
     * 一个参数的缓存key
     */
    String ONE_PARAM_CACHE_KEY = "#root.method.name + ':'+ #root.args[0]";

    /**
     * 两个参数的缓存key
     */
    String TWO_PARAM_CACHE_KEY = "#root.method.name + ':' + #root.args[0] + ':' + #root.args[1]";

    /**
     * 30 天
     */
    int ONE_MONTH = 3600 * 24 * 30;

    /**
     * 缓存key之间的分隔符 比如缓存key是这个格式
     */
    String CACHE_DELIMITER = ":";


    interface User {
        /**
         * 缓存前缀
         */
        String CACHE_NAME_PREFIX = "test:user";
        /**
         * 过期时间(单位:秒) 目前设置的是:8小时
         */
        int CACHE_EXPIRE_IN_SECONDS = 28800;
        /**
         * 缓存名
         */
        String CACHE_NAME = CACHE_NAME_PREFIX + "#" + CACHE_EXPIRE_IN_SECONDS;

        /**
         * 已缓存的方法
         */
        Collection<String> CACHED_METHOD_NAMES = getCachedMethodNames(UserService.class);
    }

    /**
     * 获取缓存方法
     *
     * @param classArray 带有缓存注解的类
     * @return 已缓存的方法名
     */
    static Collection<String> getCachedMethodNames(Class... classArray) {
        return Arrays.stream(classArray)
                     .flatMap(aClass -> getCacheableMethodNames(aClass).stream())
                     .collect(toList());
    }

    /**
     * 获取指定类中,所有缓存的方法名
     *
     * @param clazz 带有 {@link Cacheable} 注解的类
     * @return 指定类中 带有 {@link Cacheable} 注解的方法名称列表
     */
    static List<String> getCacheableMethodNames(Class clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(clazz.getMethods())
                     .filter(method -> method.getAnnotation(Cacheable.class) != null)
                     .map(Method::getName)
                     .collect(toList());
    }


    /**
     * 解析缓存key
     *
     * @param key 格式:   mam-service:product::fundList:Query(current=2, size=10, ascs=null, descs=F_AVGRETURN_SINCEFOUND):FundListReq(fundType=1202)
     * @return 缓存key的描述
     */
    static CacheKey parseCacheKey(String key) {
        List<String> list = Arrays.stream(key.split(CACHE_DELIMITER)).collect(toList());
        String cacheName = list.get(0) + CACHE_DELIMITER + list.get(1);
        String cacheKey = String.join(CACHE_DELIMITER, ListUtil.sub(list, 3, list.size()));
        return new CacheKey(cacheName, cacheKey);
    }

    @Data
    @AllArgsConstructor
    class CacheKey {
        /**
         * 缓存名
         */
        String cacheName;

        /**
         * 缓存key值
         */
        String cacheKey;
    }
}
