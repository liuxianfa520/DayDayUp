package com.liuxianfa.junit.internetfeecalc.processor;

import com.liuxianfa.junit.internetfeecalc.SwFee;
import com.liuxianfa.junit.internetfeecalc.SwType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LiuXianfa
 * @email LiuXianfa
 * @date 2021/11/23 10:55
 */
@Data
@Slf4j
public class ProcessContext {

    /**
     * 上机时间
     */
    private Date start;

    /**
     * 下机时间
     */
    private Date end;

    /**
     * 网费明细
     */
    private List<SwFee> feeList = new ArrayList<>();

    public SwFee addFee(Date start, Date end, int unitPrice, int price, SwType type) {
        return addFee(start, end, unitPrice, price, type, null);
    }

    public SwFee addFee(SwFee fee) {
        if (fee == null) {
            throw new RuntimeException("参数fee不能为空!");
        }
        boolean add = feeList.add(fee);
        log.info("添加明细:开始={},结束={},单价={},总价={},类型={},描述={}", fee.getStart(),
                 fee.getEnd(), fee.getUnitPrice(), fee.getCost(), fee.getType(), fee.getDesc());
        return fee;
    }

    public SwFee addFee(Date start, Date end, int unitPrice, int price, SwType type, String desc) {
        SwFee swFee = new SwFee()
                .setStart(start)
                .setEnd(end)
                .setUnitPrice(unitPrice)
                .setCost(price)
                .setType(type)
                .setDesc(desc);
        feeList.add(swFee);
        log.info("添加明细:开始={},结束={},单价={},总价={},类型={},描述={}", start, end, unitPrice, price, type, desc);
        return swFee;
    }
}
