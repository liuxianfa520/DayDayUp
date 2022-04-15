package com.liuxianfa.junit.springboot.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static com.liuxianfa.junit.springboot.cache.CacheTypeInterface.ONE_PARAM_CACHE_KEY;

/**
 * @date 2022/4/15 17:21
 */
@Service
@Data
@Accessors(chain = true)
@Slf4j
@RequiredArgsConstructor
public class UserService {

    /**
     * 需要在启动类上:  @EnableCaching  使用这个注解
     */
    @Cacheable(cacheNames = CacheTypeInterface.User.CACHE_NAME, keyGenerator = "simpleDateKeyGenerator")
    public String getUserNameByDate(Date birthday) {
        System.out.println("todo:从数据库中查询用户名称.....");
        return "张三";
    }

    /**
     * 删除缓存
     */
    @CacheEvict(cacheNames = CacheTypeInterface.User.CACHE_NAME, key = ONE_PARAM_CACHE_KEY)
    // 在bladex中可以使用:  @CacheRemoveAll(cacheName = CacheTypeInterface.User.CACHE_NAME)
    public boolean remove(Integer id) {
        return true;
    }

    /**
     * 更新缓存
     */
    @CachePut(cacheNames = CacheTypeInterface.User.CACHE_NAME, key = ONE_PARAM_CACHE_KEY)
    public HashMap<Object, Object> update(Integer id) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "张三");
        return map;
    }
}
