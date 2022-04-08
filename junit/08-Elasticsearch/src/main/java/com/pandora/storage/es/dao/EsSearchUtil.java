package com.pandora.storage.es.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;

import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.exception.MeheException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.collection.CollUtil.getFirst;
import static java.util.stream.Collectors.toList;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/5/21 15:12
 */
@Slf4j
public class EsSearchUtil {

    private static final ConcurrentHashMap<String, ScrollIdTimeOut> scrollIdTimeOutMap = new ConcurrentHashMap();

    private static final long scrollTimeOutInMin = 1L;

    private EsSearchUtil() {
    }

    public static List<DataStorage> search(String indexName, SearchRequest searchRequest, RestHighLevelClient client) {
        return search(indexName, searchRequest, client, Collections.emptyMap());
    }

    /**
     * 搜索数据
     *
     * <pre>
     * 如果需要分页搜索，并且需要第一页数据，则会开启scroll。后续相同查询条件不同的分页条件查询都会转为scroll搜索。
     * </pre>
     *
     * @param indexName
     * @param searchRequest
     * @param client
     * @param optionalParam
     * @return
     */
    public static List<DataStorage> search(String indexName, SearchRequest searchRequest, RestHighLevelClient client, Map<String, Object> optionalParam) {
        if (log.isDebugEnabled()) {
            log.info("查询请求：{}", searchRequest.toString());
        }
        SearchSourceBuilder searchSourceBuilder = searchRequest.source();

        String scrollId = MapUtil.getStr(optionalParam, "scrollId");
        Long scrollTimeOutInSeconds = MapUtil.getLong(optionalParam, "scrollTimeOutInSeconds");

        // 开启scroll：这种情况，是调用了 DataQuery#enableScroll() 方法开启了scroll分页模式。
        boolean enableScroll = scrollId == null && (scrollTimeOutInSeconds != null && scrollTimeOutInSeconds > 0);
        if (enableScroll) {
            // 设置scroll超时时间
            searchRequest.scroll(TimeValue.timeValueSeconds(scrollTimeOutInSeconds));
            // 由于scroll模式和from不能同时存在，所以这里使用反射把from字段设置成默认值。
            if (searchSourceBuilder.from() != -1) {
                try {
                    Field from = searchSourceBuilder.getClass().getDeclaredField("from");
                    from.setAccessible(true);
                    from.set(searchSourceBuilder, -1);
                } catch (NoSuchFieldException e) {
                    throw new MeheException("没有此字段异常！[from字段]", e);
                } catch (IllegalAccessException e) {
                    throw new MeheException("字段权限访问异常！[from字段]", e);
                }
            }
        } else if (scrollId != null) {
            return scroll(indexName, scrollId, scrollTimeOutInSeconds, client);
        }

        // 如果需要查询总数，则设置不需要返回doc
        Boolean hasSelectCount = MapUtil.getBool(optionalParam, "hasSelectCount");
        if (hasSelectCount != null && hasSelectCount) {
            searchRequest.source().fetchSource(false);
        }

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return getDataStorages(indexName, hasSelectCount, searchResponse);
        } catch (IOException e) {
            throw new MeheException(String.format("查询文档失败。异常信息：【%s】", e.getMessage()), e);
        }
    }

    private static List<DataStorage> getDataStorages(String indexName, Boolean hasSelectCount, SearchResponse searchResponse) {
        if (searchResponse.isTimedOut()) {
            throw new MeheException("查询文档超时。");
        }

        if (RestStatus.OK != searchResponse.status()) {
            return Collections.EMPTY_LIST;
        }

        log.info("Elasticsearch Search indexName=[{}],totalHits = [{}]", indexName, searchResponse.getHits().getTotalHits());
        SearchHits hits = searchResponse.getHits();
        if (hasSelectCount != null && hasSelectCount) {
            // todo:这里解析用户传入的别名，使用别名。
            DataStorage dataStorage = new DataStorage().put("count", hits.getTotalHits().value);
            return CollectionUtil.newArrayList(dataStorage);
        }

        // 在第一个和最后一个数据对象中，都有个 _scroll_id 字段。用于存储翻页的scrollId字段。
        List<DataStorage> dataStorages = Arrays.stream(hits.getHits()).map(hit -> new DataStorage(hit.getSourceAsMap())).collect(toList());

        String scrollId = searchResponse.getScrollId();
        if (!dataStorages.isEmpty() && scrollId != null) {
            log.info("SearchResponse scrollId={}", scrollId);
            dataStorages.get(0).put("_scroll_id", scrollId);
            dataStorages.get(dataStorages.size() - 1).put("_scroll_id", scrollId);
        }
        return dataStorages;
    }

    /**
     * scroll分页查询
     *
     * @param indexName
     * @param scrollId
     * @param scrollTimeOutInSeconds
     * @param client
     * @return
     */
    private static List<DataStorage> scroll(String indexName, String scrollId, Long scrollTimeOutInSeconds, RestHighLevelClient client) {
        try {
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(new Scroll(TimeValue.timeValueSeconds(scrollTimeOutInSeconds)));
            SearchResponse searchResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
            return getDataStorages(indexName, false, searchResponse);
        } catch (IOException e) {
            throw new MeheException(String.format("查询文档失败。异常信息：【%s】", e.getMessage()), e);
        }
    }

    /**
     * 查询es索引的mapping
     *
     * @param indexName
     * @param client
     * @return <pre> 返回格式：{@code
     *
     *     {
     *         "user_name":{
     *             "type":"text",
     *             "fields":{
     *                 "keyword":{
     *                     "ignore_above":256,
     *                     "type":"keyword"
     *                 }
     *             }
     *         },
     *         "salary":{
     *             "type":"long"
     *         },
     *         "birth_date":{
     *             "type":"date"
     *         }
     *     }
     *
     * }
     * </pre>
     */
    public static Map<String, Map<String, Object>> getMapping(String indexName, RestHighLevelClient client) {
        try {
            GetMappingsRequest request = new GetMappingsRequest();
            request.indices(indexName);

            GetMappingsResponse getMappingResponse = client.indices().getMapping(request, RequestOptions.DEFAULT);
            Map<String, Object> indexMapping = getMappingResponse.mappings().get(indexName).sourceAsMap();
            if (CollectionUtil.isNotEmpty(indexMapping) && indexMapping.containsKey("properties")) {
                return (Map<String, Map<String, Object>>) indexMapping.get("properties");
            }
            throw new MeheException("返回索引Mapping格式不正确。Mapping：" + JSON.toJSONString(indexMapping));
        } catch (IOException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new MeheException(String.format("不存在索引[%s]", indexName));
            }
            throw new MeheException(String.format("查询索引mapping失败。异常信息：【%s】", e.getMessage()));
        }
    }

    /**
     * <pre>
     * 【如果value为字符串类型或者由字符串组成的list/数组，就把key转换成keyword】
     * 对于以下情况，把key转成 key + ".keyword"
     * 1、value为字符串类型
     * 2、value为字符串组成的list/set
     * 3、value为字符串组成的数组
     *
     * 原因：
     * 对于term查询/order排序等，如果需要查询的字段为字符串类型，在查询es的时候需要把key拼接上.keyword后缀
     *
     * 举例：如果SQL查询为： where name = 'zhangsan'
     * 则Elasticsearch DSL Query为：
     * {
     *     "size":100,
     *     "query":{
     *         "term":{
     *             "title.keyword":{
     *                 "boost":1.0,
     *                 "value":"北京下雪了"
     *             }
     *         }
     *     }
     * }
     * </pre>
     */
    public static String keyWord(String key, Object value) {
        return isString(value) ? keyWord(key, true) : key;
    }

    public static String keyWord(String key, boolean isKeyWord) {
        return isKeyWord ? key + ".keyword" : key;
    }

    /**
     * <pre>
     * 判断参数是否为 {@link String} 类型
     * 如果value为 {@link Collection} 则判断集合中元素是否为 {@link String} 类型
     * </pre>
     *
     * @param value
     * @return 如果参数或者集合中元素为字符串类型，则返回true
     */
    private static boolean isString(Object value) {
        return value instanceof String
                || (value instanceof String[] && ((String[]) value).length > 0 && isString(((String[]) value)[0]))
                || (value instanceof Object[] && ((Object[]) value).length > 0 && isString(((Object[]) value)[0]))
                || (value instanceof Collection && isString(getFirst(((Collection) value))));

    }

    public static String getKeyMappingType(Map<String, Map<String, Object>> mapping, String key) {
        validateContainsKey(mapping, key);
        return MapUtil.getStr(mapping.get(key), "type");
    }

    /**
     * 根据索引的mapping，判断指定key，是否为text类型。
     *
     * @param mapping
     * @param key
     * @return
     */
    public static boolean isText(Map<String, Map<String, Object>> mapping, String key) {
        validateContainsKey(mapping, key);
        return "text".equals(getKeyMappingType(mapping, key));
    }

    /**
     * 根据索引mapping，判断指定key，是否为keyword
     *
     * @param mapping
     * @param key
     * @return
     */
    public static boolean isKeyword(Map<String, Map<String, Object>> mapping, String key) {
        validateContainsKey(mapping, key);
        Object type = JSONPath.compile("$" + key + ".fields.keyword.type").eval(mapping);
        return "keyword".equals(type);
    }

    public static void validateContainsKey(Map<String, Map<String, Object>> mapping, String key) {
        if (!mapping.containsKey(key)) {
            throw new MeheException("当前ES索引中不包含此字段！key=" + key);
        }
    }

    /**
     * <pre>
     * 自定义 {@link SearchSourceBuilder}类的hashCode方法。
     * 注意：和 {@link SearchSourceBuilder#hashCode()} 方法相比，忽略 from 和 size 两个属性。
     * </pre>
     *
     * @param s SearchSourceBuilder
     * @return
     */
    @Deprecated
    public static String searchSourceBuilderHashCode(SearchSourceBuilder s) {
        return String.valueOf(Objects.hash(s.aggregations(), s.explain(), s.fetchSource(), s.docValueFields(), s.storedFields(), s.highlighter(),
                                           s.indexBoosts(), s.minScore(), s.postFilter(), s.query(), s.rescores(), s.scriptFields(),
                                           s.sorts(), s.searchAfter(), s.slice(), s.stats(), s.suggest(), s.terminateAfter(), s.timeout(), s.trackScores(), s.version(),
                                           s.seqNoAndPrimaryTerm(), s.profile(), s.ext(), s.collapse(), s.trackTotalHitsUpTo()));
    }
}
