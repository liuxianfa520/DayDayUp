package com.liuxianfa.junit;

import java.util.Arrays;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/3/31 13:03
 */
public class 奇数在前偶数在后 {

    /**
     * int[] a = {1,2,3,4,5};
     * <p>
     * 要求:把奇数放前面,偶数放后面
     */
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5};
        ;
        int low = 0;
        int high = a.length - 1;
        /**想法：
         * 1.遍历。将奇数放在左边，偶数放在右边
         * 2.先遍历左边，直到出现不是奇数的时候为止
         * 3.再遍历右边，直到出现不是偶数的时候为止
         * 4.交换位置
         */
        while (low < high) {
            int i = low;
            //从数组的左边开始遍历
            while (a[i] % 2 != 0) {
                low++;
                i = low;
            }

            int index = i;//当前出现的偶数的位置下标
            i = high;
            //从数组的右边开始遍历
            while (a[i] % 2 == 0) {
                high--;
                i = high;
            }
            //交换
            if (low < high) {
                int tmp = a[index];
                a[index] = a[i];
                a[i] = tmp;
            }
        }
        System.out.println(Arrays.toString(a));
    }
}
