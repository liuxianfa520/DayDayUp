package com.liuxianfa.junit.internetfeecalc.testcase;

import com.alibaba.fastjson.JSON;
import com.liuxianfa.junit.internetfeecalc.processor.BaoYeProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.Chain;
import com.liuxianfa.junit.internetfeecalc.processor.KaiJiProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.LowestCostProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.ProcessContext;
import com.liuxianfa.junit.internetfeecalc.processor.UnitPriceProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.YouHuiProcessor;

import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 14:45
 */
public class InterNetFeeCalcTest {
    Date start = new DateTime("2021-11-01 00:01");
    Date end = new DateTime("2021-11-01 08:59");

    // 开机费
    int kaijiPrice = 5;
    KaiJiProcessor kaiJiProcessor = new KaiJiProcessor(kaijiPrice);


    // 单价
    int unitPrice = 10;
    UnitPriceProcessor unitPriceProcessor = new UnitPriceProcessor(unitPrice);


    // 包夜
    int baoyePrice = 30;
    String baoyeStart = "23:00";
    String baoyeEnd = "06:00";
    BaoYeProcessor baoYeProcessor = new BaoYeProcessor(baoyeStart, baoyeEnd, baoyePrice, unitPrice);

    // 最低消费配置
    int lowestCostPrice = 500;
    boolean lowestCostEnable = true;
    LowestCostProcessor lowestCostProcessor = new LowestCostProcessor(lowestCostPrice, lowestCostEnable);


    // 优惠时段
    YouHuiProcessor youHuiProcessor;

    {
        ArrayList<YouHuiProcessor.YouHuiConfig> youHuiConfigs = new ArrayList<>();
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("周末上午优惠(周六日,每天8点~12点)", "0 0 8 ? * SAT-SUN", "0 0 12 ? * SAT-SUN", 8));
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("工作日优惠(周一~周四,每天8点~20点)", "0 0 8 ? * MON-THU", "0 0 20 ? * MON-THU", 8));
        youHuiProcessor = new YouHuiProcessor(youHuiConfigs);
    }

    @Test
    public void testMain() {
        // 注意:由于实现的原因,处理器顺序是固定的.
        Chain chain = new Chain(kaiJiProcessor, lowestCostProcessor, baoYeProcessor, youHuiProcessor, unitPriceProcessor);
        ProcessContext processContext = new ProcessContext();
        int fee = kaiJiProcessor.process(start, end, chain, processContext);
        System.out.printf("网费=%s%n", fee);
        System.out.println(JSON.toJSONString(processContext.getFeeList()));
    }

    private static boolean isIn(LocalTime time, LocalTime begin, LocalTime end) {
        return (time.isAfter(begin) || time.equals(begin)) && (time.isBefore(end) || time.equals(end));
    }

    /**
     * 判断是否重叠的
     *
     * @param s
     * @param e
     */
    private boolean isOverlap(LocalTime s, LocalTime e, LocalTime baoYeStart, LocalTime baoYeEnd) {

        boolean in = isIn(baoYeStart, s, e);
        boolean in1 = isIn(baoYeEnd, s, e);
        boolean in2 = isIn(s, baoYeStart, baoYeEnd);
        boolean in3 = isIn(e, baoYeStart, baoYeEnd);
        return in || in1 || in2 || in3;
    }

    public static boolean isSameDay(Date t1, Date t2) {
        return DateUtil.isSameDay(t1, t2);
    }
}