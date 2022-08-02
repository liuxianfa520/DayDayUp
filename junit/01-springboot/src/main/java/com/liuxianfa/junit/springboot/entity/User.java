package com.liuxianfa.junit.springboot.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @date 2022/8/1 9:55
 */
@Data
@Accessors(chain = true)
public class User {

    Integer id;

    String name;

    /**
     * fixme:这里有个大坑.需要注意!!!
     *  前端传参时,需要传 fageXx 才能接收到参数.
     */
    String fAgeXx;

    /**
     * fixme:这里有个大坑.需要注意!!!
     *  前端传参时,需要传 faddressXx 才能接收到参数.
     */
    String fAddressXx;
}
