package com.liuxianfa.junit.springboot.cache;

import com.liuxianfa.junit.springboot.SpringbootApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/4/15 17:23
 */
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void name() throws InterruptedException {
        // 执行之后,看控制台,只有有一次打印出:  "todo:从数据库中查询用户名称....."
        String userNameByDate = userService.getUserNameByDate(new Date());

        String userNameByDate2 = userService.getUserNameByDate(new Date());
    }
}
