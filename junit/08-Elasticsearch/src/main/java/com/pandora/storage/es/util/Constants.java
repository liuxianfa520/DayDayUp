package com.pandora.storage.es.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/16 15:37
 */
public class Constants {


    public static final String KEY_NAME_OBJECT_ID = "objectId";


    /**
     * 系统保留字段
     */
    public static final Set<String> Reserved_Column = new HashSet<String>() {{
        // fixme: 这里使用 id 字段作为自增id，可能会和用户自定义的id字段重复。所以这里需要该成其他字段,比如 xid
        add("id");
        add("appId");
        add(KEY_NAME_OBJECT_ID);
        add("className");
        add("serverData");
        add("createTime");
        add("updateTime");
    }};

}
