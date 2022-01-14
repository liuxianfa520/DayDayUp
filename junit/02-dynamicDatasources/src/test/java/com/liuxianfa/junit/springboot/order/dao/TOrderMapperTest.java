package com.liuxianfa.junit.springboot.order.dao;

import com.liuxianfa.junit.springboot.DynamicDatasourcesApplication;
import com.liuxianfa.junit.springboot.order.entity.TOrder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import cn.hutool.json.JSONUtil;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/14 15:56
 */
@SpringBootTest(classes = DynamicDatasourcesApplication.class)
@RunWith(value = SpringRunner.class)
public class TOrderMapperTest {

    @Autowired
    TOrderMapper tOrderMapper;

    @Test
    public void selectByExample() {
        List<TOrder> tOrders = tOrderMapper.selectByExample(null);
        System.out.println(JSONUtil.toJsonPrettyStr(tOrders));
    }
}
