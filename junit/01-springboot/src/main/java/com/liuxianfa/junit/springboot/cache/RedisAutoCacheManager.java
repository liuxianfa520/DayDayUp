package com.liuxianfa.junit.springboot.cache;

import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

/**
 * org.springblade.core.redis.config.RedisAutoCacheManager
 *
 * @date 2022/4/15 17:42
 */
public class RedisAutoCacheManager extends RedisCacheManager {
    public RedisAutoCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
    }

    @Override
    @NonNull
    protected RedisCache createRedisCache(@NonNull String name, @Nullable RedisCacheConfiguration cacheConfig) {
        if (StrUtil.isNotBlank(name) && name.contains("#")) {
            String[] cacheArray = name.split("#");
            if (cacheArray.length < 2) {
                return super.createRedisCache(name, cacheConfig);
            } else {
                String cacheName = cacheArray[0];
                if (cacheConfig != null) {
                    Duration cacheAge = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
                    cacheConfig = cacheConfig.entryTtl(cacheAge);
                }

                return super.createRedisCache(cacheName, cacheConfig);
            }
        } else {
            return super.createRedisCache(name, cacheConfig);
        }
    }
}
