package com.liuxianfa.junit.internetfeecalc.testcase;

import com.alibaba.fastjson.JSON;
import com.liuxianfa.junit.internetfeecalc.processor.BaoYeProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.Chain;
import com.liuxianfa.junit.internetfeecalc.processor.KaiJiProcessor;
import com.liuxianfa.junit.internetfeecalc.processor.ProcessContext;
import com.liuxianfa.junit.internetfeecalc.processor.UnitPriceProcessor;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Date;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 14:45
 */
public class InterNetFeeCalcTest {
    private Date start = new DateTime("2021-11-01 12:00");
    private Date end = new DateTime("2021-11-02 12:00");

    private String baoyeStart = "23:00";
    private String baoyeEnd = "06:00";


    private int unitPrice = 10;
    private int baoyePrice = 30;
    private int kaijiPrice = 5;

    private BaoYeProcessor baoYeProcessor = new BaoYeProcessor(baoyeStart, baoyeEnd, baoyePrice, unitPrice);

    @Test
    public void testMain() {
        UnitPriceProcessor unitPriceProcessor = new UnitPriceProcessor(unitPrice);
        BaoYeProcessor baoYeProcessor = new BaoYeProcessor(baoyeStart, baoyeEnd, baoyePrice, unitPrice);
        KaiJiProcessor kaiJiProcessor = new KaiJiProcessor(kaijiPrice);

        Chain chain = new Chain(kaiJiProcessor, baoYeProcessor, unitPriceProcessor);
        ProcessContext processContext = new ProcessContext();
        int fee = kaiJiProcessor.process(start, end, chain, processContext);
        System.out.printf("网费=%s%n", fee);
        System.out.println(JSON.toJSONString(processContext.getFeeList()));
    }

    private static boolean isIn(LocalTime time, LocalTime begin, LocalTime end) {
        return (time.isAfter(begin) || time.equals(begin)) && (time.isBefore(end) || time.equals(end));
    }

    /**
     * {@link BaoYeProcessor#canProcessor(Date, Date)}
     */
    @Test
    public void 判断一个时间段是否和另一个时间段重叠() {
        // 先判断start~end之间是否等于或超过1天,如果是:返回true
        long days = DateUtil.betweenDay(start, end, false);
        if (days >= 1) {
            System.out.println("超过1天,重叠了");
            return;
        }
        // 判断包含
        LocalTime startLocalTime = DateUtil.toLocalDateTime(this.start).toLocalTime();
        LocalTime endLocalTime = DateUtil.toLocalDateTime(this.end).toLocalTime();

        LocalTime baoYeStart = baoYeProcessor.getBaoYeStart();
        LocalTime baoYeEnd = baoYeProcessor.getBaoYeEnd();

        if (baoYeProcessor.baoYeIsSameDay()) {
            if (isOverlap(startLocalTime, endLocalTime, baoYeStart, baoYeEnd)) {
                System.out.println("重叠了");
            }
        } else {
            LocalTime endOfDay = LocalTime.parse("23:59:59");
            LocalTime beginOfDay = LocalTime.parse("00:00:00");
            if (isOverlap(startLocalTime, endLocalTime, baoYeStart, endOfDay) || isOverlap(startLocalTime, endLocalTime, beginOfDay, baoYeEnd)) {
                System.out.println("重叠了");
            }
        }
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
