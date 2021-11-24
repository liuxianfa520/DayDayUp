package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwFee;
import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/24 11:05
 */
public class LowestCostProcessor implements InternetFeeCalcProcessor {

    // todo:尚未考虑包间需要乘以机器数量的情况.
    @Getter
    @Setter
    private int price;

    @Getter
    @Setter
    private boolean enable;

    /**
     * 是否已经计算过 (最低消费计算一次就行了)
     */
    private boolean hasBeenCalculated;

    public LowestCostProcessor(int price, boolean enable) {
        this.price = price;
        this.enable = enable;
    }

    @Override
    public boolean canProcessor(Date start, Date end) {
        return enable && !hasBeenCalculated;
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        if (!enable || hasBeenCalculated) {
            return 0;
        }

        hasBeenCalculated = true;
        // 调用执行器链,先去执行完所有的处理器
        chain.doProcess(start, end, context);

        // 网费总和
        int total = context.getFeeList().stream()
                           .mapToInt(SwFee::getCost)
                           .sum();

        // 网费小于最低消费
        if (total < price) {
            List<SwFee> feeList = context.getFeeList().stream()
                                         .filter(swFee -> Objects.equals(SwType.kaijifei, swFee.getType()))
                                         .collect(Collectors.toList());
            context.setFeeList(feeList);
            return context.addFee(context.getStart(), context.getEnd(), price, price, SwType.lowest_cost).getCost();
        }
        return total;
    }
}
