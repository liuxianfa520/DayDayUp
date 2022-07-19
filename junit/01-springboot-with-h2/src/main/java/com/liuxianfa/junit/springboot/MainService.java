package com.liuxianfa.junit.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
    @Transactional(rollbackOn = Exception.class)
    public void runWithTx() {
        List<User> all = userMapper.findAll();

        User user = all.get(0);
        user.setName(user.getName() + "update");
        userMapper.save(user);

        List<Future> collect = all.stream()
                                  .map(user1 -> userService.run(user1))
                                  .collect(Collectors.toList());


        for (Future future : collect) {
            Object o = future.get();
        }
    }
}
