package com.liuxianfa.junit.springboot.service;

import com.liuxianfa.junit.springboot.order.dao.TOrderMapper;
import com.liuxianfa.junit.springboot.order.entity.TOrder;
import com.liuxianfa.junit.springboot.user.dao.TUserMapper;
import com.liuxianfa.junit.springboot.user.entity.TUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
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
        // 如果使用的是 AbstractRoutingDataSource ,则这里会直接报错.Table 'user.t_order' doesn't exist
        int insert1 = tOrderMapper.insert(order);
        System.out.println("保存完毕");
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrderAndUserError() {
        TOrder order = new TOrder().setOrderNo(1).setSkuId(4);
        // 如果使用的是 AbstractRoutingDataSource ,则这里会直接报错.Table 'user.t_order' doesn't exist
        // 因为在创建事务时,也就是在进入此service方法之前,就会创建一个jdbcConnection,
        // 而创建jdbcConnection时,还没指定datasource,
        // 所以会使用默认的数据库 datasource —— userDataSource
        // user的数据库中,肯定没有t_order表,就直接报错了.
        tOrderMapper.insert(order);


        TUser user = new TUser().setName("success transaction").setAge(12);
        tUserMapper.insert(user);
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


    /**
     * 事务传播特性:不需要事务
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TOrder getById(Integer id) {
        return tOrderMapper.selectByPrimaryKey(id);
    }

    @Autowired
    OrderService orderService;

    @Transactional(rollbackFor = Exception.class)
    public void NOT_SUPPORTED() {
        // 此时,这个不需要事务的,可以正常查询order库
        System.out.println(JSONUtil.toJsonStr(orderService.getById(3)));

        // 用户新增也可以
        tUserMapper.insert(new TUser().setAge(1).setName("张三"));

        int a = 1 / 0;// 搞个异常,也能正常回滚user表的新增操作.

        // 此时,这个不需要事务的,可以正常查询order库
        System.out.println(JSONUtil.toJsonStr(orderService.getById(3)));
    }
}
