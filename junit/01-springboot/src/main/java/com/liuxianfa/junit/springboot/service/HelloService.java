package com.liuxianfa.junit.springboot.service;

import org.springframework.stereotype.Service;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/17 16:40
 */
@Service
public class HelloService {
    public void hello(String name) {
        System.out.println("hello:" + name);
    }
}
