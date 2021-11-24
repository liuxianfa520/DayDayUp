package com.liuxianfa.junit.internetfeecalc.processor;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/24 18:37
 */
public class YouHuiProcessor implements InternetFeeCalcProcessor {

    List<YouHuiConfig> youHuiConfigList;

    public YouHuiProcessor(List<YouHuiConfig> youHuiConfigList) {
        this.youHuiConfigList = youHuiConfigList;
        if (this.youHuiConfigList == null) {
            this.youHuiConfigList = Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        final LocalDateTime dateTime = CronExpression.parse("0 0/12 * * * ?").next(LocalDateTime.now());
        System.out.println(dateTime);
    }

    @Override
    public boolean canProcessor(Date start, Date end) {


        return false;
    }

    /**
     * 获取下一次优惠开始时间
     */
    public Date getNextYouHuiStartDate(Date from) {
        if (CollUtil.isEmpty(youHuiConfigList)) {
            return null;
        }
        LocalDateTime _from = LocalDateTimeUtil.of(from);

        LocalDateTime nextYouHuiStartDate = null;
        for (YouHuiConfig youHuiConfig : youHuiConfigList) {
            LocalDateTime next = CronExpression.parse(youHuiConfig.getStartCron()).next(_from);
            if (nextYouHuiStartDate == null || next.isBefore(nextYouHuiStartDate)) {
                nextYouHuiStartDate = next;
            }
        }
        return new DateTime(nextYouHuiStartDate);
    }

    /**
     * 获取下一次优惠结束时间
     */
    public Date getNextYouHuiEndDate(Date from) {
        if (CollUtil.isEmpty(youHuiConfigList)) {
            return null;
        }

        LocalDateTime _from = LocalDateTimeUtil.of(getNextYouHuiStartDate(from));

        LocalDateTime nextYouHuiEndDate = null;
        for (YouHuiConfig youHuiConfig : youHuiConfigList) {
            LocalDateTime next = CronExpression.parse(youHuiConfig.getEndCron()).next(_from);
            if (nextYouHuiEndDate == null || next.isBefore(nextYouHuiEndDate)) {
                nextYouHuiEndDate = next;
            }
        }
        return new DateTime(nextYouHuiEndDate);
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        return 0;
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

        int price;
    }


}
