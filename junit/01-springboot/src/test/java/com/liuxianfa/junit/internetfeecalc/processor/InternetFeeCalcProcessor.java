package com.liuxianfa.junit.internetfeecalc.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import cn.hutool.core.date.DateUnit;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:47
 */
public interface InternetFeeCalcProcessor {

    // 是否能处理
    boolean canProcessor(Date start, Date end);

    // 计算金额
    int process(Date start, Date end, Chain chain, ProcessContext context);


    /**
     * 开始和结束时间之间,相隔多少小时,不满1小时按1小时算
     *
     * @param begin 开始时间
     * @param end   结束时间
     */
    default int betweenHour(Date begin, Date end) {
        return new BigDecimal(end.getTime() - begin.getTime())
                .divide(new BigDecimal(DateUnit.HOUR.getMillis()), 0, RoundingMode.UP)
                .intValue();
    }
}
