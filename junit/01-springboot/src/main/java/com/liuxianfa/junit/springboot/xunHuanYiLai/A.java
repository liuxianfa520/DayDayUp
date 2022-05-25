package com.liuxianfa.junit.springboot.xunHuanYiLai;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

import cn.hutool.extra.spring.SpringUtil;

@Service
@Validated
public class A {

//    @Autowired
//    private B b;

    public void say(@NotBlank String name) {
        System.out.println("A say: hello " + name);
    }

    public void bSay(@NotBlank String name) {
        // 直接使用SpringUtil,不会循环依赖.
        SpringUtil.getBean(B.class).say(name);
    }
}