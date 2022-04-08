package com.pandora.storage.es.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/10 11:18
 */

@Data
public class OrderBy implements Serializable {
    public static final long serialVersionUID = 1L;


    private String key;

    /**
     * 正序或倒序 ：ASC/DESC  默认空格(正序)
     */
    private String order = "";

    public OrderBy() {
    }


    public OrderBy(String key) {
        this.key = key;
    }

    public OrderBy(String key, String order) {
        this.key = key;
        this.order = order;
    }

    public static OrderBy asc(String key) {
        return new OrderBy(key);
    }

    public static OrderBy desc(String key) {
        return new OrderBy(key, "DESC");
    }

}
