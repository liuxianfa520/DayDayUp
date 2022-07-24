package com.liuxianfa.junit.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

/**
 * @author LiuXianfa
 * @date 3/17 16:44
 */
@RestController
public class HelloController {

    @Autowired
    MainService mainService;
    @Autowired
    UserMapper userMapper;


    @RequestMapping("hello")
    @Transactional(rollbackOn = Exception.class)
    public String hello() {
        init();
        mainService.runWithTx();
        return "success";
    }

    @RequestMapping("deleteAll")
    public String deleteAll() {
        userMapper.deleteAll();
        return "success";
    }

    public void init() {
        userMapper.deleteAll();
        System.out.println(userMapper.save(new User().setId(-1L).setName("zzz1").setAge(-1)));
        System.out.println(userMapper.save(new User().setId(2L).setName("zzz2").setAge(2)));
        System.out.println(userMapper.save(new User().setId(3L).setName("zzz3").setAge(3)));
        System.out.println(userMapper.save(new User().setId(4L).setName("zzz4").setAge(4)));
    }


}
