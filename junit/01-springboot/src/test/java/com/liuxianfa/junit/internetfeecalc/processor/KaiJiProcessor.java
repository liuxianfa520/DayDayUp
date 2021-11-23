package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.Date;

import lombok.Getter;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 11:14
 */
public class KaiJiProcessor implements InternetFeeCalcProcessor {

    @Getter
    int price;

    public KaiJiProcessor(int price) {
        this.price = price;
    }

    /**
     * 是否已经计算过 (开机费只能计算一次)
     */
    boolean hasBeenCalculated = false;

    @Override
    public boolean canProcessor(Date start, Date end) {
        return !hasBeenCalculated;
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        if (!hasBeenCalculated) {
            hasBeenCalculated = true;
            context.addFee(new Date(), null, price, price, SwType.kaijifei);
            // 开机费自身 加上 其他处理器的处理逻辑
            return price + chain.doProcess(start, end, context);
        }
        return 0;
    }
}
