package com.pandora.storage.es.service.impl;


import com.pandora.storage.es.dao.EsStorageRepository;
import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.service.EsStorageService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/23 11:23
 */
@Service
public class EsStorageServiceImpl implements EsStorageService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EsStorageServiceImpl.class);

    @Autowired
    EsStorageRepository esStorageRepository;

    @Override
    public String index(String indexName, String id, String source) {
        return esStorageRepository.index(indexName, id, source);
    }

    @Override
    public List<DataStorage> get(String indexName) {
        return esStorageRepository.get(indexName);
    }

    @Override
    public DataStorage get(String indexName, String id) {
        return esStorageRepository.get(indexName, id);
    }

    @Override
    public boolean exist(String indexName, String id) {
        return esStorageRepository.exist(indexName, id);
    }

    @Override
    public String delete(String indexName, String id) {
        return esStorageRepository.delete(indexName, id);
    }

    @Override
    public boolean deleteAll(String indexName) {
        try {
            get(indexName).forEach(dataStorage -> delete(indexName, dataStorage.getObjectId()));
            return true;
        } catch (Exception e) {
            logger.warn("删除所有文档发生异常。异常信息：%s", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteIndex(String indexName) {
        return esStorageRepository.deleteIndex(indexName);
    }

    @Override
    public DataStorage update(String indexName, String id, DataStorage content, Boolean fetchSource) {
        return esStorageRepository.update(indexName, id, content, null, fetchSource);
    }

    @Override
    public DataStorage upsert(String indexName, String id, DataStorage upsertContent, Boolean fetchSource) {
        return esStorageRepository.update(indexName, id, null, upsertContent, fetchSource);
    }

    @Override
    public DataStorage getByObjectId(Long objectId) {
        return esStorageRepository.getByObjectId(objectId);
    }
}
