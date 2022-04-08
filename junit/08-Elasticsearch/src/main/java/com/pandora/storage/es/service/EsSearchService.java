package com.pandora.storage.es.service;


import com.pandora.storage.es.entity.DataStorage;

import java.util.List;
import java.util.Map;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/28 17:03
 */
public interface EsSearchService {

    List<DataStorage> selectByServerData(Map queryMap);
}
