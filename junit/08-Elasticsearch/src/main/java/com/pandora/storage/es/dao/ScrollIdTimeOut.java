package com.pandora.storage.es.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/5/27 17:43
 */
@Data
@Builder
@AllArgsConstructor
public class ScrollIdTimeOut {

    private String scrollId;

    /**
     * scrollId到期时间
     */
    private long expireTime = System.currentTimeMillis() + 60000;

    public ScrollIdTimeOut touchScrollId() {
        expireTime = System.currentTimeMillis() + 60000;
        return this;
    }

    public boolean isExpire() {
        return System.currentTimeMillis() > expireTime;
    }
}
