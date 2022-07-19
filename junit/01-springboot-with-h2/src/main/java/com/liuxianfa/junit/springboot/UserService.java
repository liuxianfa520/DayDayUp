package com.liuxianfa.junit.springboot;

import com.alibaba.fastjson.JSON;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import lombok.SneakyThrows;

/**
 * @author xianfaliu2@creditease.cn
 * @date 7/19 22:43
 */
@Service
public class UserService {

    @SneakyThrows
    @Async
    public Future run(User user) {
        Thread.sleep(2000);
        System.out.println(JSON.toJSONString(user, true));
        if (user.getAge() == -1) {
            throw new RuntimeException("年龄不合法.");
        }
        return new AsyncResult(user);
    }
}
