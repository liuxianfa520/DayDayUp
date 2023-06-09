package com.liuxianfa.junit.springboot.service;

import com.liuxianfa.junit.springboot.order.dao.TOrderMapper;
import com.liuxianfa.junit.springboot.order.entity.TOrder;
import com.liuxianfa.junit.springboot.user.dao.TUserMapper;
import com.liuxianfa.junit.springboot.user.entity.TUser;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LiuXianfa
 * @date 2022/1/19 10:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    final TUserMapper tUserMapper;

    final TOrderMapper tOrderMapper;


    @Transactional(rollbackFor = Exception.class)
    public void saveOrderAndUser() {
        TUser user = new TUser();
        user.setName("success transaction");
        user.setAge(12);
        int insert = tUserMapper.insert(user);

        TOrder order = new TOrder();
        order.setOrderNo(1);
        order.setSkuId(4);
        int insert1 = tOrderMapper.insert(order);
        System.out.println("保存完毕");
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveOrderAndUserRollback() {
        TUser user = new TUser();
        user.setName("rollback");
        user.setAge(12);
        int insert = tUserMapper.insert(user); // user库事务无法回滚

        TOrder order = new TOrder();
        order.setOrderNo(1);
        order.setSkuId(4);
        int insert1 = tOrderMapper.insert(order);// order库事务可以被回滚.


        int a = 1 / 0;
        System.out.println("保存完毕");
    }

}
