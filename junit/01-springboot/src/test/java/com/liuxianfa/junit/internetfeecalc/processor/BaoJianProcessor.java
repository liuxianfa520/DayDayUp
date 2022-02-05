package com.liuxianfa.junit.internetfeecalc.processor;

import java.util.Date;

import lombok.Data;

/**
 * 包间 计费
 *
 * @author anxiaole
 * @date 2/5 19:27
 */
@Data
public class BaoJianProcessor implements InternetFeeCalcProcessor {
    /**
     * 包间里机器的数量
     */
    private int numberOfMachines;

    /**
     * 单个机子网费计算规则
     */
    private Chain chain;

    public BaoJianProcessor(int numberOfMachines, Chain chain) {
        this.numberOfMachines = numberOfMachines;
        this.chain = chain;
    }

    @Override
    public boolean canProcessor(Date start, Date end) {
        return true;
    }

    @Override
    public int process(Date start, Date end, Chain chain, ProcessContext context) {
        return process(start, end, context);
    }

    public int process(Date start, Date end, ProcessContext context) {
        InternetFeeCalcProcessor kaiJiProcessor = this.getChain().getList().get(0);
        int fee = kaiJiProcessor.process(start, end, this.getChain(), context);
        return numberOfMachines * fee;
    }
}