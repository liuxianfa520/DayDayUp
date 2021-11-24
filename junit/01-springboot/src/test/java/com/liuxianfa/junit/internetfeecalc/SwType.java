package com.liuxianfa.junit.internetfeecalc;

/**
 * @author LiuXianfa
 * @email xianfaliu2@creditease.cn
 * @date 2021/11/23 10:57
 */
public enum SwType {

    kaijifei,
    lowest_cost,
    normal,
    normal_youhui,
    baoye,
    /**
     * 包夜时段,但是网费并没有超过包夜总价.
     */
    baoYeTime_but_unit_price
}
