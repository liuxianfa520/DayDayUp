package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 包夜处理器
 * <p>
 * 流程图:https://docs.qq.com/flowchart/DQVRBRXBISmJteXBy
 *
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 11:07
 */
@Slf4j
public class BaoYeProcessor implements InternetFeeCalcProcessor {
    private static final LocalTime endOfDay = LocalTime.parse("23:59:59");
    private static final LocalTime beginOfDay = LocalTime.parse("00:00:00");

    @Getter
    LocalTime baoYeStart;
    @Getter
    LocalTime baoYeEnd;
    @Getter
    int price;
    /**
     * 按小时收费单价
     */
    @Getter
    int unitPrice;

    /**
     * @param baoYeStart 格式   23:00
     * @param baoYeEnd   格式   6:00
     */
    public BaoYeProcessor(String baoYeStart, String baoYeEnd, int price, int unitPrice) {
        this.baoYeStart = LocalTime.parse(baoYeStart);
        this.baoYeEnd = LocalTime.parse(baoYeEnd);
        this.price = price;
        this.unitPrice = unitPrice;
    }

    public BaoYeProcessor(LocalTime baoYeStart, LocalTime baoYeEnd, int price, int unitPrice) {
        this.baoYeStart = baoYeStart;
        this.baoYeEnd = baoYeEnd;
        this.price = price;
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean canProcessor(Date start, Date end) {
        // 判断 上网时间段内,是否有包夜时段.
        return isOverlapWithBaoYe(start, end);
    }


    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        int total = 0;
        // 一、先处理  start ~ baoYeStart  上网的费用.
        // 包夜开始时间
        Date baoYeStartTime = getBaoYeStartDateTime(start);
        if (isBefore(start, baoYeStartTime)) {
            total += context.addFee(start, baoYeStartTime, unitPrice, betweenHour(start, baoYeStartTime) * unitPrice, SwType.normal).getCost();
        }


        /*
         * 二、再处理 :
         * -----+--------------------------+--------------+------------------
         *     上机/包夜开始相同         下机           包夜结束
         *
         * 或:
         *
         * -----+-----------------------------------------+----------------+-
         *     上机/包夜开始相同                        包夜结束         下机
         */


        // 包夜结束时间
        Date baoYeEndTime = getBaoYeEndDateTime(start);

        // 是否在包夜时段内下机的
        boolean endInBaoYeDuration = isBefore(end, baoYeEndTime);

        Date endTmp = endInBaoYeDuration ? end : baoYeEndTime;

        // 判断单价上网网费是否小于包夜价格
        int cost = betweenHour(baoYeStartTime, endTmp) * unitPrice;
        if (cost < price) {
            // 使用单价上网计算规则
            total += context.addFee(baoYeStartTime, endTmp, unitPrice, cost, SwType.baoYeTime_but_unit_price).getCost();
        } else {
            // 使用包夜价格计算
            total += context.addFee(baoYeStartTime, endTmp, price, price, SwType.baoye).getCost();
        }

        if (!endInBaoYeDuration) {
            // 计算 baoyeEnd ~ end 时间段网费
            total += chain.doProcess(baoYeEndTime, end, context);
        }
        return total;
    }


    /**
     * 指定日期from,获取日期对应的包夜结束时间
     */
    private Date getBaoYeEndDateTime(Date from) {
        LocalDateTime tmp = DateUtil.toLocalDateTime(from)
                                    .withHour(baoYeEnd.getHour())
                                    .withMinute(baoYeEnd.getMinute())
                                    .withSecond(baoYeEnd.getSecond());
        if (baoYeIsSameDay()) {
            // 日期:from    时间:baoYeEnd
            return DateUtil.date(tmp);
        }
        // 日期 from+1天   时间:baoYeEnd
        return DateUtil.date(tmp.plusDays(1));
    }

    /**
     * 指定日期from,获取日期对应的包夜开始时间
     */
    private Date getBaoYeStartDateTime(Date from) {
        return DateUtil.date(DateUtil.toLocalDateTime(from)
                                     .withHour(baoYeStart.getHour())
                                     .withMinute(baoYeStart.getMinute())
                                     .withSecond(baoYeStart.getSecond()));
    }

    /**
     * 判断包夜开始和结束时间,是在同一天. <br/> 比如  00:00 ~ 06:00
     */
    public boolean baoYeIsSameDay() {
        return baoYeStart.isBefore(baoYeEnd);
    }

    /**
     * 判断包夜开始和结束时间,不是同一天
     */
    public boolean baoYeIsNotSameDay() {
        //比如 6:00     在       23:00  之前,则说明:包夜的结束时间不是在同一天
        return baoYeEnd.isBefore(baoYeStart);
    }


    /**
     * 判断上网时间和包夜时间是否重叠
     *
     * @param start 上网开始时间
     * @param end   上网结束时间
     */
    private boolean isOverlapWithBaoYe(Date start, Date end) {
        return isOverlap(start, end, getBaoYeStartDateTime(start), getBaoYeEndDateTime(start));
    }

    /**
     * 判断两个时间段是否重叠 详见:https://www.cnblogs.com/AndyJee/p/4537251.html
     */
    private boolean isOverlap(Date start1, Date end1, Date start2, Date end2) {
        return end1.getTime() > start2.getTime() && start1.getTime() < end2.getTime();
    }

    private boolean isBefore(Date date, Date date2) {
        return date.before(date2) || date.equals(date2);
    }

    private boolean isAfter(Date date, Date date2) {
        return date.after(date2) || date.equals(date2);
    }
}
