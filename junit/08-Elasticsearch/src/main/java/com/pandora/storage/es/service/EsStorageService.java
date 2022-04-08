package com.pandora.storage.es.service;


import com.pandora.storage.es.entity.DataStorage;

import java.util.List;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/23 11:15
 */
public interface EsStorageService {

    /**
     * @param indexName
     * @param id
     * @param source
     * @return 成功索引的文档id
     */
    String index(String indexName, String id, String source);

    List<DataStorage> get(String indexName);

    DataStorage get(String indexName, String id);

    boolean exist(String indexName, String id);

    /**
     * @param indexName
     * @param id
     * @return 从es被删除文档的id, 也就是objectId
     */
    String delete(String indexName, String id);

    boolean deleteAll(String indexName);

    boolean deleteIndex(String indexName);

    DataStorage update(String indexName, String id, DataStorage content, Boolean fetchSource);

    DataStorage upsert(String indexName, String id, DataStorage upsertContent, Boolean fetchSource);

    DataStorage getByObjectId(Long objectId);
}
