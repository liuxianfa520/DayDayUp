package com.liuxianfa.junit.springboot;

import com.liuxianfa.junit.springboot.UserMapper;
import com.liuxianfa.junit.springboot.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuXianfa
 * @date 3/17 16:44
 */
@RestController
public class HelloController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("hello")
    public String hello(String name) {

        User zzz = userMapper.save(new User().setName("zzz").setAge(1));
        System.out.println(zzz);
        return "controller hello " + name;
    }


}
