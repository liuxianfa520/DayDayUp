package com.liuxianfa.junit.springboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.SneakyThrows;

@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;


    @Before
    public void init() {
        userMapper.deleteAll();
        System.out.println(userMapper.save(new User().setId(-1L).setName("zzz1").setAge(-1)));
        System.out.println(userMapper.save(new User().setId(2L).setName("zzz2").setAge(2)));
        System.out.println(userMapper.save(new User().setId(3L).setName("zzz3").setAge(3)));
        System.out.println(userMapper.save(new User().setId(4L).setName("zzz4").setAge(4)));
    }


    @Autowired
    MainService mainService;

    @SneakyThrows
    @Test
    public void name() {
        mainService.runWithTx();

        Thread.sleep(100 * 1000);
    }
}
