package com.pandora.storage.es.dao;


import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.entity.OrderBy;

import java.util.List;
import java.util.Map;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/28 13:48
 */
public interface EsSearchMapper {

    List<DataStorage> selectByServerData(String indexName,
                                         Map whereJson,
                                         List<OrderBy> orderBys,
                                         Integer skip,
                                         Integer limit,
                                         List<String> keys,
                                         List<String> groupBy,
                                         Map<String, Object> optionalParam);
}
