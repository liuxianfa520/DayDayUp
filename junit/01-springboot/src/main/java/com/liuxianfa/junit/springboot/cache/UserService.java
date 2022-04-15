package com.liuxianfa.junit.springboot.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xianfaliu2@creditease.cn
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
    @Cacheable(cacheNames = "01-springboot:user:birthday", keyGenerator = "simpleDateKeyGenerator")
    public String getUserNameByDate(Date birthday) {
        System.out.println("todo:从数据库中查询用户名称.....");
        return "张三";
    }
}
