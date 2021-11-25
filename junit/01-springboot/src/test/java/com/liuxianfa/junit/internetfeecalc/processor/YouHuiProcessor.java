package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.pattern.CronPatternUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/24 18:37
 */
public class YouHuiProcessor implements InternetFeeCalcProcessor {

    List<YouHuiConfig> youHuiConfigList;

    private ThreadLocal<NextYouHuiTime> nextYouHuiTimeThreadLocal = new ThreadLocal<>();

    public YouHuiProcessor(List<YouHuiConfig> youHuiConfigList) {
        this.youHuiConfigList = youHuiConfigList;
        if (this.youHuiConfigList == null) {
            this.youHuiConfigList = Collections.emptyList();
        }
    }

    @Override
    public boolean canProcessor(Date start, Date end) {
        return isOverlapWithYouHui(start, end);
    }

    /**
     * 上网时间段中是否有优惠时段
     *
     * @param start 上机时间
     * @param end   下机时间
     * @return true:上网时间段中有优惠时段,false:上网时间段中没有优惠时段
     */
    private boolean isOverlapWithYouHui(Date start, Date end) {
        Date s = start;
        while (true) {
            NextYouHuiTime nextYouHuiTime = getNextYouHuiTime(s);
            // 没有下次优惠时段,则结束
            if (nextYouHuiTime == null) {
                return false;
            }
            Date nextYouHuiStartDate = nextYouHuiTime.getStart();
            Date nextYouHuiEndDate = nextYouHuiTime.getEnd();

            // 下次优惠开始前,就已经下机了.
            if (end.before(nextYouHuiStartDate)) {
                return false;
            }
            // 如果上机时间段内有和优惠时段重叠,则返回true
            if (isOverlap(start, end, nextYouHuiStartDate, nextYouHuiEndDate)) {
                nextYouHuiTimeThreadLocal.set(nextYouHuiTime);
                return true;
            }
            s = nextYouHuiStartDate;
        }
    }

    /**
     * 获取下一次优惠时间段
     */
    public NextYouHuiTime getNextYouHuiTime(Date from) {
        if (CollUtil.isEmpty(youHuiConfigList)) {
            return null;
        }
        NextYouHuiTime nextYouHuiTime = null;
        for (YouHuiConfig youHuiConfig : youHuiConfigList) {
            Date next = cronNext(from, youHuiConfig.getStartCron());
            if (nextYouHuiTime == null || next.before(nextYouHuiTime.getStart())) {
                if (nextYouHuiTime == null) {
                    nextYouHuiTime = new NextYouHuiTime();
                }
                nextYouHuiTime.setStart(next).setYouHuiConfig(youHuiConfig);
            }
        }
        if (nextYouHuiTime != null) {
            nextYouHuiTime.setEnd(cronNext(nextYouHuiTime.getStart(), nextYouHuiTime.getYouHuiConfig().getEndCron()));
        }
        return nextYouHuiTime;
    }

    private Date cronNext(Date from, String cron) {
//        return new DateTime(CronExpression.parse(cron).next(LocalDateTimeUtil.of(from)));
        // fixme:感觉这种在年底时,会出现bug?
        return CronPatternUtil.nextDateAfter(new CronPattern(cron), from, true);
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        int total = 0;
        // 一、先处理  start ~ youHuiStart  上网的费用.
        NextYouHuiTime nextYouHuiTime = nextYouHuiTimeThreadLocal.get();
        int youHuiPrice = nextYouHuiTime.getYouHuiConfig().getYouHuiPrice();
        int unitPrice = nextYouHuiTime.getYouHuiConfig().getUnitPrice();
        Date youHuiStartTime = nextYouHuiTime.getStart();
        if (isBefore(start, youHuiStartTime)) {
            total += context.addFee(start, youHuiStartTime, unitPrice, betweenHour(start, youHuiStartTime) * unitPrice, SwType.normal).getCost();
        }


        // 二、优惠时段计费
        // 优惠结束时间
        Date youHuiEndTime = nextYouHuiTime.getEnd();
        // 是否在优惠时段内下机的
        boolean endInYouHuiDuration = isBefore(end, youHuiEndTime);
        Date endTmp = endInYouHuiDuration ? end : youHuiEndTime;
        // 判断 start 是否在 youHuiStart 之后
        Date startTmp = start.after(youHuiStartTime) ? start : youHuiStartTime;
        String desc = String.format("启用的优惠名称为:[%s]", nextYouHuiTime.getYouHuiConfig().getName());
        total += context.addFee(startTmp, endTmp, youHuiPrice, betweenHour(startTmp, endTmp) * youHuiPrice, SwType.youhui, desc).getCost();


        // 三、如果youHuiEnd之后还没下机,则计算 youHuiEnd ~ end 时间段网费
        if (!endInYouHuiDuration) {
            total += chain.doProcess(youHuiEndTime, end, context);
        }
        return total;
    }

    @Data
    @AllArgsConstructor
    public static class YouHuiConfig {
        /**
         * 优惠名称
         */
        String name;
        /**
         * 优惠开始时间的cron表达式
         */
        String startCron;
        /**
         * 优惠结束时间的cron表达式
         */
        String endCron;

        int youHuiPrice;

        int unitPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class NextYouHuiTime {
        Date start;
        Date end;

        YouHuiConfig youHuiConfig;
    }

}
