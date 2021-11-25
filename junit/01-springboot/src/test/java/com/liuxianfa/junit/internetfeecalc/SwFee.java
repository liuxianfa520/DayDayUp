package com.liuxianfa.junit.internetfeecalc;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:56
 */
@Data
@Accessors(chain = true)
public class SwFee {

    Date start;

    Date end;

    int unitPrice;

    int cost;

    SwType type;

    String desc;
}
