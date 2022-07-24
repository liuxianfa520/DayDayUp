package com.liuxianfa.junit.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

/**
 * @author xianfaliu2@creditease.cn
 * @date 7/19 22:46
 */
@Service

public class MainService {


    @Autowired
    UserMapper userMapper;


    @Autowired
    UserService userService;

    @SneakyThrows
    public void runWithTx() {
        System.out.println("runWithTx:  " + Thread.currentThread().getName());
        List<User> all = userMapper.findAll();

        List<Future> collect = all.stream()
                                  .map(user1 -> userService.run(user1))
                                  .collect(Collectors.toList());


        for (Future future : collect) {
            try {
                Object o = future.get();
                System.out.println("SpringBoot异步任务返回:" + JSONUtil.toJsonPrettyStr(o));
            } catch (Exception e) {
                System.out.println("SpringBoot异步任务异常:" + ExceptionUtil.stacktraceToString(e));
                //因为是在try catch 里面所以需要代码手动去踹他一脚,如果没用就不需要会自动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
    }
}
