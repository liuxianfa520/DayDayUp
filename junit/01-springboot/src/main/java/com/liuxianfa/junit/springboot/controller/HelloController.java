package com.liuxianfa.junit.springboot.controller;

import com.liuxianfa.junit.springboot.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 3/17 16:44
 */
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {
        helloService.hello(name);

        return "controller hello " + name;
    }
}
