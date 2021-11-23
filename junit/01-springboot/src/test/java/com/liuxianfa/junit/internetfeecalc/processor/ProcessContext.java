package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwFee;
import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:55
 */
@Slf4j
public class ProcessContext {

    @Getter
    private List<SwFee> feeList = new ArrayList<>();

    public SwFee addFee(Date start, Date end, int unitPrice, int price, SwType type) {
        SwFee swFee = new SwFee()
                .setStart(start)
                .setEnd(end)
                .setUnitPrice(unitPrice)
                .setCost(price)
                .setType(type);
        feeList.add(swFee);
        log.info("添加明细:开始={},结束={},单价={},总价={},类型={}", start, end, unitPrice, price, type);
        return swFee;
    }
}
