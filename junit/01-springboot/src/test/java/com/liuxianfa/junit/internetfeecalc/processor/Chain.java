package com.liuxianfa.junit.internetfeecalc.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:52
 */
public class Chain {

    @Getter
    private List<InternetFeeCalcProcessor> list = new ArrayList<>();

    public Chain(InternetFeeCalcProcessor... processors) {
        if (processors != null && processors.length > 0) {
            Collections.addAll(list, processors);
        }
        putUnitPriceProcessorLast();
    }

    public Chain addProcessor(InternetFeeCalcProcessor processor) {
        list.add(processor);
        putUnitPriceProcessorLast();
        return this;
    }

    public Chain addProcessor(InternetFeeCalcProcessor... processors) {
        if (processors != null && processors.length > 0) {
            Collections.addAll(list, processors);
        }
        putUnitPriceProcessorLast();
        return this;
    }

    /**
     * 注意:在构建处理器链的时候,需要保证 {@link UnitPriceProcessor} 处理器在处理器链中的最后一个.
     */
    private void putUnitPriceProcessorLast() {
        UnitPriceProcessor unitPriceProcessor = null;
        Iterator<InternetFeeCalcProcessor> it = list.iterator();
        while (it.hasNext()) {
            InternetFeeCalcProcessor processor = it.next();
            if (processor instanceof UnitPriceProcessor) {
                unitPriceProcessor = (UnitPriceProcessor) processor;
                it.remove();
            }
        }
        if (unitPriceProcessor != null) {
            list.add(unitPriceProcessor);
        }
    }

    public int doProcess(Date s, Date e, ProcessContext context) {
        for (InternetFeeCalcProcessor processor : list) {
            if (processor.canProcessor(s, e)) {
                return processor.process(s, e, this, context);
            }
        }
        return 0;
    }

}
