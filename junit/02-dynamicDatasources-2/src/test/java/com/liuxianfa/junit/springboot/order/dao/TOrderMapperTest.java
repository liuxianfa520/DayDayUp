package com.liuxianfa.junit.springboot.order.dao;

import com.liuxianfa.junit.springboot.DynamicDatasourcesApplication2;
import com.liuxianfa.junit.springboot.order.entity.TOrder;
import com.liuxianfa.junit.springboot.service.OrderService;
import com.liuxianfa.junit.springboot.user.dao.TUserMapper;
import com.liuxianfa.junit.springboot.user.entity.TUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import cn.hutool.json.JSONUtil;

/**
 * @author LiuXianfa
 * @date 2022/1/14 15:56
 */
@SpringBootTest(classes = DynamicDatasourcesApplication2.class)
@RunWith(value = SpringRunner.class)
public class TOrderMapperTest {

    @Autowired
    TOrderMapper tOrderMapper;

    @Autowired
    TUserMapper tUserMapper;

    @Autowired
    OrderService orderService;

    @Test
    public void orderService() {
        orderService.saveOrderAndUser();
        System.out.println("done");
    }


    @Test
    public void saveOrderAndUserRollback() {
        orderService.saveOrderAndUserRollback();
        System.out.println("done");
    }

    @Test
    public void selectByExample() {
        List<TOrder> tOrders = tOrderMapper.selectByExample(null);
        System.out.println(JSONUtil.toJsonPrettyStr(tOrders));
    }

    @Test
    public void tUserMapper() {
        List<TUser> tUsers = tUserMapper.selectByExample(null);
        System.out.println(JSONUtil.toJsonPrettyStr(tUsers));
    }
}
