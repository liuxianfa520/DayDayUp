package com.liuxianfa.junit.springboot.order.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TOrder implements Serializable {
    private Integer id;
    private Integer orderNo;
    private Integer skuId;
    private static final long serialVersionUID = 1L;
}