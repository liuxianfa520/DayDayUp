package com.liuxianfa.junit.internetfeecalc.testcase;

import com.liuxianfa.junit.internetfeecalc.processor.YouHuiProcessor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/24 18:57
 */
public class YouHuiProcessorTest {

    private static YouHuiProcessor youHuiProcessor;

    static {
        ArrayList<YouHuiProcessor.YouHuiConfig> youHuiConfigs = new ArrayList<>();
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("周末上午优惠(周六日,每天8点~12点)", "0 0 8 ? * SAT-SUN", "0 0 12 ? * SAT-SUN", 8));
        youHuiConfigs.add(new YouHuiProcessor.YouHuiConfig("工作日优惠(周一~周四,每天8点~20点)", "0 0 8 ? * MON-THU", "0 0 20 ? * MON-THU", 8));
        youHuiProcessor = new YouHuiProcessor(youHuiConfigs);
    }

    @Test
    public void getNextYouHuiStartDate() {
        System.out.println(youHuiProcessor.getNextYouHuiStartDate(new Date()));
        System.out.println(youHuiProcessor.getNextYouHuiEndDate(new Date()));
    }
}
