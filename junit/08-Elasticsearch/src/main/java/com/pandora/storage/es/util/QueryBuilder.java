package com.pandora.storage.es.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/14 17:05
 */
public class QueryBuilder {


    public static Map<String, String> pointerQuery(String className, String objectId) {
        HashMap<String, String> pointerQuery = new HashMap<>();
        pointerQuery.put("__type", "Pointer");
        pointerQuery.put("className", className);
        pointerQuery.put("objectId", objectId);
        return pointerQuery;
    }

}
