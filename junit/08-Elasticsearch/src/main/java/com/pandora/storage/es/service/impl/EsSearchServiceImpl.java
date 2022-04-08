package com.pandora.storage.es.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pandora.storage.es.api.IndexNameUtils;
import com.pandora.storage.es.dao.EsSearchMapper;
import com.pandora.storage.es.dao.EsStorageRepository;
import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.entity.OrderBy;
import com.pandora.storage.es.entity.Pointer;
import com.pandora.storage.es.exception.MeheException;
import com.pandora.storage.es.exception.MeheExceptionEnum;
import com.pandora.storage.es.service.EsSearchService;
import com.pandora.storage.es.util.QueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

import static java.util.stream.Collectors.toList;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/6 15:45
 */
@Service
public class EsSearchServiceImpl implements EsSearchService {
    private static final Logger logger = LoggerFactory.getLogger(EsSearchServiceImpl.class);

    @Autowired
    EsSearchMapper mapper;

    @Autowired
    EsStorageRepository esStorageRepository;

    @Override
    public List<DataStorage> selectByServerData(Map queryMap) {
        String appId = MapUtil.getStr(queryMap, "appId");
        String className = MapUtil.getStr(queryMap, "className");
        String include = MapUtil.getStr(queryMap, "include");
        String where = MapUtil.getStr(queryMap, "where");
        String order = MapUtil.getStr(queryMap, "order");
        Integer skip = MapUtil.getInt(queryMap, "skip");
        Integer limit = MapUtil.getInt(queryMap, "limit");
        String keys = MapUtil.getStr(queryMap, "keys");
        String groupBy = MapUtil.getStr(queryMap, "groupBy");
        String scrollId = MapUtil.getStr(queryMap, "scrollId");
        Long scrollTimeOutInSeconds = MapUtil.getLong(queryMap, "scrollTimeOutInSeconds");

        JSONObject whereJson = JSON.parseObject(where);
        if (CollectionUtils.isEmpty(whereJson)) {
            return simpleSelectByServerData(appId, className, include, where, order, skip, limit, keys, groupBy, scrollId, scrollTimeOutInSeconds);
        }

        for (; ; ) {
            for (String key : whereJson.keySet()) {
                Object value = whereJson.get(key);
                if (value instanceof Map && ((Map) value).containsKey("$select")) {
                    // 包含$select子查询：匹配另一个查询的返回值
                    JSONObject innerQuery = (JSONObject) ((Map) value).get("$select");
                    List<Object> innerResult = this.$select(appId, include, innerQuery);

                    whereJson.put(key, MapUtil.of("$in", innerResult));
                } else if (value instanceof Map && ((Map) value).containsKey("$dontSelect")) {
                    // 包含$dontSelect子查询：排除另一个查询的返回值
                    JSONObject innerQuery = (JSONObject) ((Map) value).get("$dontSelect");
                    List<Object> innerResult = this.$select(appId, include, innerQuery);

                    whereJson.put(key, MapUtil.of("$nin", innerResult));
                } else if (value instanceof Map && ((Map) value).containsKey("$inQuery")) {
                    // 如果你想获取对象，这个对象的一个字段指向的对象需要另一个查询来指定，你可以使用 $inQuery 操作符。
                    JSONObject $inQuery = (JSONObject) ((Map) value).get("$inQuery");
                    List<String> innerResultObjectIds = this.$inQuery(appId, $inQuery);
                    if (CollectionUtils.isEmpty(innerResultObjectIds)) {
                        // 子查询结果为空，则总查询结果就是空。
                        return Collections.emptyList();
                    }
                    Map<String, String> pointerQuery = QueryBuilder.pointerQuery($inQuery.getString("className"), innerResultObjectIds.get(0));

                    whereJson.put(key, pointerQuery);
                } else if (value instanceof Map && ((Map) value).containsKey("$notInQuery")) {
                    JSONObject $notInQuery = (JSONObject) ((Map) value).get("$notInQuery");
                    List<String> innerResultObjectIds = this.$inQuery(appId, $notInQuery);
                    if (CollectionUtils.isEmpty(innerResultObjectIds)) {
                        // 子查询结果为空，则总查询结果就是空。
                        return Collections.emptyList();
                    }
                    Map<String, String> pointerQuery = QueryBuilder.pointerQuery($notInQuery.getString("className"), innerResultObjectIds.get(0));
                    pointerQuery.put("notInQueryPlaceholder", "notInQueryPlaceholder"); // 用来当做判断的占位符

                    whereJson.put(key, pointerQuery);
                } else {
                    // value中不包含子查询
                    return simpleSelectByServerData(appId, className, include, JSON.toJSONString(whereJson), order, skip, limit, keys, groupBy, scrollId, scrollTimeOutInSeconds);
                }
            }
        }
    }

    /**
     * @param appId
     * @param $inQuery 子查询查询参数
     * @return 子查询结果的objectId字段list
     */
    private List<String> $inQuery(String appId, JSONObject $inQuery) {
        // $inQuery子查询中没有key字段,只会用到objectId字段进行外层的查询。详见AVQuery.whereMatchesQuery()方法
        String innerSelectKey = "objectId";
        List<DataStorage> objInstanceEntitys = simpleSelectByServerData(appId, $inQuery.getString("className"),
                                                                        null,
                                                                        JSON.toJSONString($inQuery.getJSONObject("where")),
                                                                        JSON.toJSONString($inQuery.get("order")),
                                                                        $inQuery.getInteger("skip"),
                                                                        $inQuery.getInteger("limit"),
                                                                        innerSelectKey, null, null, null);
        return objInstanceEntitys.stream()
                                 .map(objInstanceEntity -> (String) objInstanceEntity.get(innerSelectKey))
                                 .collect(toList());
    }

    private List<Object> $select(String appId, String include, JSONObject query) {
        JSONObject innerQuery = query.getJSONObject("query");
        String innerSelectKey = query.getString("key");
        List<DataStorage> objInstanceEntitys = simpleSelectByServerData(appId, innerQuery.getString("className"),
                                                                        include,
                                                                        JSON.toJSONString(innerQuery.get("where")),
                                                                        JSON.toJSONString(innerQuery.get("order")),
                                                                        innerQuery.getInteger("skip"),
                                                                        innerQuery.getInteger("limit"),
                                                                        innerSelectKey, null, null, null);
        return objInstanceEntitys.stream()
                                 .map(objInstanceEntity -> objInstanceEntity.get(innerSelectKey))
                                 .collect(toList());
    }

    /**
     * @param className
     * @param include                当一个对象包含另一个 class 的数据而不想进行额外的查询，可在一个查询上使用 include ; include 可以用 dot 符号（.）来获取多级关系。
     * @param where
     * @param order
     * @param skip
     * @param limit
     * @param keys                   通过 selectKeys 指定需要返回的属性; todo dot 符号（.）还可用于 selectKeys 以限制返回的关联对象的属性
     * @param groupBy                分组字段
     * @param scrollId
     * @param scrollTimeOutInSeconds @return
     */
    private List<DataStorage> simpleSelectByServerData(String appId, String className, String include, String where, String order,
                                                       Integer skip, Integer limit, String keys, String groupBy, String scrollId, Long scrollTimeOutInSeconds) {
        JSONObject whereJson = JSON.parseObject(where);
        if (whereJson == null) {
            whereJson = new JSONObject();
        }

        this.whereCheck(whereJson);

        List<OrderBy> orderList = Collections.emptyList();
        if (!StrUtil.isBlankOrUndefined(order)) {
            orderList = Arrays.stream(order.split(","))
                              .map(item -> item.startsWith("-") ? OrderBy.desc(item.substring(1)) : OrderBy.asc(item))
                              .collect(toList());
        }

        List<String> keyList = StrUtil.isBlankOrUndefined(keys) ? Collections.EMPTY_LIST : Arrays.asList(keys.split(","));
        // selectByServerData方法，处理selectKey 层级结构
        ArrayList<String> selectKeyListWithKeyPath = getSelectKeyListWithKeyPath(include, keyList, null);

        List<String> groupByKeys = StringUtils.isEmpty(groupBy) ? Collections.EMPTY_LIST : Arrays.stream(groupBy.split(",")).collect(toList());

        boolean hasSelectCount = keyList.stream().anyMatch(key -> key.toLowerCase().contains("count("));
        HashMap<String, Object> optionalParam = new HashMap<>();
        optionalParam.put("hasSelectCount", hasSelectCount);
        optionalParam.put("hasGroup", groupByKeys.size() >= 1);
        optionalParam.put("scrollId", scrollId);
        optionalParam.put("scrollTimeOutInSeconds", scrollTimeOutInSeconds);

        try {
            return mapper.selectByServerData(IndexNameUtils.indexName(appId, className), whereJson, orderList, skip, limit, selectKeyListWithKeyPath, groupByKeys, optionalParam)
                         .stream()
                         .filter(Objects::nonNull)
                         .peek(objInstanceEntity -> {
                             objInstanceEntity.transferAndRemoveServerData();
                             if (hasIncludeNeedReplaceProcess(include, objInstanceEntity)) {
                                 processIncludePointer(include, keyList, objInstanceEntity, null);
                             }
                         })
                         .collect(toList());
        } catch (Exception e) {
            String indexName = IndexNameUtils.indexName(appId, className);
            if (e instanceof NullPointerException) {
                throw new MeheException("查询Elasticsearch异常。NullPointerException", e);
            } else if (e.getMessage().contains("Unknown index") || e.getMessage().contains("index_not_found_exception")) {
                logger.warn("Elasticsearch中不存在索引indexName=【{}】", indexName);
                return Collections.emptyList();
            } else if (false) {
                // 在这里添加其他异常。

            }
            throw new MeheException("查询Elasticsearch异常。异常信息：" + e.getMessage(), e);
        }
    }

    /**
     * 如果查询时，指定了include，则把查询结果中的Pointer对象都查询出数据。
     *
     * @param include
     * @param dataStorage
     */
    private void processIncludePointer(String include, List<String> keyList, DataStorage dataStorage, String keyPath) {
        String className = dataStorage.getClassName();
        String appId = dataStorage.getAppId();
        String indexName = IndexNameUtils.indexName(appId, className);

        for (Object key : dataStorage.keySet()) {
            Object value = dataStorage.get(key);
            if (include.contains(key.toString()) && Pointer.typeIsPointer(value)) {
                keyPath = keyPath == null ? key.toString() : keyPath + "." + key;
                logger.info("【处理includePointer】当前需要处理的indexName=【{}】字段keyPath=【{}】", indexName, keyPath);
                ArrayList<String> selectKeyList = this.getSelectKeyListWithKeyPath(include, keyList, keyPath);

                DataStorage entityByObjectId = esStorageRepository.get(indexName, ((JSONObject) value).getString("objectId"));
                if (true) {
                    // todo:entityByObjectId.transferAndRemoveServerData().transferToPointer();
                    throw new RuntimeException("这里？？、");
                }

                dataStorage.put(key, entityByObjectId);
                if (hasIncludeNeedReplaceProcess(include, entityByObjectId)) {
                    processIncludePointer(include, keyList, entityByObjectId, keyPath);
                }
            }
        }
        logger.info("dataStorage keyset loop finish");
    }

    /**
     * 通过keyPath获取selectKey   测试方法见：GetSelectKeyListWithKeyPathTest类
     *
     * @param include 当一个对象包含另一个 class 的数据而不想进行额外的查询，可在一个查询上使用 include ; include 可以用 dot 符号（.）来获取多级关系。
     * @param keyList
     * @param keyPath
     * @return
     */
    private ArrayList<String> getSelectKeyListWithKeyPath(String include, List<String> keyList, String keyPath) {
        ArrayList<String> currentObjectKey = new ArrayList<>();
        for (String key : keyList) {
            String[] keySplit = key.split("\\.");
            if (keyPath == null) {
                if (keySplit.length == 1) {
                    currentObjectKey.add(key);
                }
            } else {
                String[] keyPathSplit = keyPath.split("\\.");
                if (keySplit.length - 1 == keyPathSplit.length && !Objects.equals(include, key)) {
                    currentObjectKey.add(keySplit[keyPathSplit.length]);
                }
            }
        }
        return currentObjectKey;
    }

    /**
     * 判断objInstanceEntity中是否有Pointer需要替换处理
     *
     * @param include
     * @param objInstanceEntity
     * @return
     */
    private boolean hasIncludeNeedReplaceProcess(String include, DataStorage objInstanceEntity) {
        if (StringUtils.isEmpty(include)) {
            return false;
        }
        for (Object key : objInstanceEntity.keySet()) {
            if (include.contains(key.toString()) && Pointer.typeIsPointer(objInstanceEntity.get(key))) {
                return true;
            }
        }
        return false;
    }

    private void whereCheck(JSONObject whereJson) {
        for (String key : whereJson.keySet()) {
            Object value = whereJson.get(key);
            // 如果使用Pointer查询，则必须传className和objectId
            if (Pointer.typeIsPointer(value)) {
                if (!((Map) value).containsKey("className") || !((Map) value).containsKey("objectId")) {
                    throw new MeheException(MeheExceptionEnum.INVALID_POINTER);
                }
            }

        }
    }
}
