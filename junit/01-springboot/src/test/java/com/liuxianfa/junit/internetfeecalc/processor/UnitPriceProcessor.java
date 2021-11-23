package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 单价处理器. 计算规则:上网小时数 * 每小时单价 <br/>
 *
 * 注意:在构建处理器链的时候,需要保证这个处理器在处理器链中的最后一个.
 *
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:47
 */
@Data
@Accessors(chain = true)
public class UnitPriceProcessor implements InternetFeeCalcProcessor {

    int unitPrice;

    public UnitPriceProcessor(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean canProcessor(Date start, Date end) {
        // 普通上网计算.需要作为最后一个处理器
        return true;
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        // 计算开始和结束之间的时间
        int hour = betweenHour(start, end);
        // 单价  *   时长
        return context.addFee(start, end, unitPrice, unitPrice * hour, SwType.normal).getCost();
    }

}
