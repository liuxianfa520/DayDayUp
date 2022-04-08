package com.pandora.storage.es.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pandora.storage.es.api.EsStorageFeignClient;
import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.service.EsSearchService;
import com.pandora.storage.es.service.EsStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/20 13:21
 */
@RestController
public class EsStorageController implements EsStorageFeignClient {

    private static final Logger logger = LoggerFactory.getLogger(EsStorageController.class);

    @Autowired
    EsStorageService esStorageService;

    @Autowired
    EsSearchService esSearchService;

    @Override
    public String test(String str) {
        return "test method return " + str;
    }

    @Override
    public String index(String indexName, String id, String source) {
        return esStorageService.index(indexName, id, source);
    }

    // todo:尚未验证
    @Override
    public List<String> indexBatch(String indexName, String sources) {
        JSONArray objects = JSON.parseArray(sources);
        return objects.stream()
                      .map(o -> esStorageService.index(indexName, ((JSONObject) o).getString("objectId"), JSON.toJSONString(o)))
                      .collect(Collectors.toList());
    }

    @Override
    public List<DataStorage> get(String indexName) {
        return esStorageService.get(indexName);
    }

    @Override
    public DataStorage get(Long objectId) {
        return esStorageService.getByObjectId(objectId);
    }

    @Override
    public DataStorage get(String indexName, String id) {
        return esStorageService.get(indexName, id);
    }

    @Override
    public boolean exist(String indexName, String id) {
        return esStorageService.exist(indexName, id);
    }

    @Override
    public String delete(String indexName, String id) {
        return esStorageService.delete(indexName, id);
    }

    @Override
    public DataStorage update(String indexName, String id, String dataStorageJsonString, boolean fetchSource) {
        return esStorageService.update(indexName, id, JSON.parseObject(dataStorageJsonString, DataStorage.class), fetchSource);
    }

    @Override
    public DataStorage upsert(String indexName, String id, String upsertContent, boolean fetchSource) {
        return esStorageService.upsert(indexName, id, JSON.parseObject(upsertContent, DataStorage.class), fetchSource);
    }

    @Override
    public List<DataStorage> query(Map<String, String> queryMap) {
        if (logger.isDebugEnabled()) {
            logger.info("Elasticsearch queryMap=【{}】", JSON.toJSONString(queryMap, true));
        }

        return esSearchService.selectByServerData(queryMap);
    }
}
