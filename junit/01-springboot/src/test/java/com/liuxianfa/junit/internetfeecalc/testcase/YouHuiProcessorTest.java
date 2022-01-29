package com.liuxianfa.junit.internetfeecalc.testcase;

import com.liuxianfa.junit.internetfeecalc.processor.YouHuiProcessor;

import org.junit.Test;

import java.util.ArrayList;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;

/**
 * @author LiuXianfa
 * @email LiuXianfa
 * @date 2021/11/24 18:57
 */
public class YouHuiProcessorTest {

    private static YouHuiProcessor youHuiProcessor;

    static {
        ArrayList<YouHuiProcessor.YouHuiConfig> youHuiConfigs = new ArrayList<>();
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("周末上午优惠(周六日,每天8点~12点)", "0 0 8 ? * SAT-SUN", "0 0 12 ? * SAT-SUN", 8,10));
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("工作日优惠(周一~周四,每天8点~20点)", "0 0 8 ? * MON-THU", "0 0 20 ? * MON-THU", 8, 10));
        youHuiProcessor = new YouHuiProcessor(youHuiConfigs);
    }

    @Test
    public void getNextYouHuiStartDate() {
        DateTime from = new DateTime("2021-11-26 05:00:00");
        YouHuiProcessor.NextYouHuiTime nextYouHuiTime = youHuiProcessor.getNextYouHuiTime(from);
        System.out.println(nextYouHuiTime.getStart());
        System.out.println(nextYouHuiTime.getEnd());
        System.out.println(JSONUtil.toJsonStr(nextYouHuiTime.getYouHuiConfig()));
    }

    @Test
    public void canProcessor() {
        DateTime start = new DateTime("2021-11-25 05:00:00");
        DateTime __end = new DateTime("2021-11-25 08:59:00");
        System.out.println(youHuiProcessor.canProcessor(start, __end));
    }
}
