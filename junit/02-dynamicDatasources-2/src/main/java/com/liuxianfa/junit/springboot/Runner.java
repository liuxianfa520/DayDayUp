package com.liuxianfa.junit.springboot;

import com.liuxianfa.junit.springboot.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/11/10 17:58
 */
@Component
public class Runner {

    @Autowired
    OrderService orderService;

    @PostConstruct
    public void run() {
//        orderService.saveOrderAndUserError();
        orderService.NOT_SUPPORTED();
    }
}
