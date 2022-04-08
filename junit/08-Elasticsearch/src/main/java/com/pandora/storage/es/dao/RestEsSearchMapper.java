package com.pandora.storage.es.dao;

import com.alibaba.fastjson.JSON;
import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.entity.OrderBy;
import com.pandora.storage.es.exception.MeheException;
import com.pandora.storage.es.util.Constants;

import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.collection.CollUtil.isEmpty;
import static com.pandora.storage.es.dao.EsSearchUtil.getMapping;
import static com.pandora.storage.es.dao.EsSearchUtil.keyWord;
import static com.pandora.storage.es.dao.EsSearchUtil.search;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/5/21 13:42
 */
@Component
@Slf4j
public class RestEsSearchMapper implements EsSearchMapper {

    private static final String[] excludesFields = {};
    @Autowired
    private RestHighLevelClient client;

    public static void main(String[] args) {
        String a = "^fasdfjasf$";

        System.out.println(a.replaceAll("[\\^\\$]", ""));
    }

    /**
     * 判断当前是and查询还是or查询
     *
     * @param currentQuery
     * @param isAndQuery
     * @param searchSourceBuilder
     */
    private static void and_or_query_choose(QueryBuilder currentQuery, Boolean isAndQuery, SearchSourceBuilder searchSourceBuilder) {
        QueryBuilder existQuery = searchSourceBuilder.query();
        if (existQuery == null) {
            searchSourceBuilder.query(currentQuery);
        } else if (!(existQuery instanceof BoolQueryBuilder)) {
            if (isAndQuery) {
                searchSourceBuilder.query(boolQuery().must(existQuery).must(currentQuery));
            } else {
                searchSourceBuilder.query(boolQuery().should(existQuery).should(currentQuery));
            }
        } else {
            if (isAndQuery) {
                searchSourceBuilder.query(((BoolQueryBuilder) existQuery).must(currentQuery));
            } else {
                searchSourceBuilder.query(((BoolQueryBuilder) existQuery).should(currentQuery));
            }
        }
    }

    @Override
    public List<DataStorage> selectByServerData(String indexName,
                                                Map whereJson,
                                                List<OrderBy> orderBys,
                                                Integer skip,
                                                Integer limit,
                                                List<String> keys,
                                                List<String> groupBy,
                                                Map<String, Object> optionalParam) {

        if (Integer.sum(skip == null ? 0 : skip, limit == null ? 0 : limit) > 1000) {
            throw new MeheException("skip + limit不能大于10000！对于深度分页请使用scroll模式。");
        }

        SearchRequest searchRequest = buildSearchSourceBuilder(indexName, whereJson, orderBys, skip, limit, keys, groupBy, optionalParam);

        List<DataStorage> result = search(indexName, searchRequest, client, optionalParam);

        return result;
    }

    private SearchRequest buildSearchSourceBuilder(String indexName,
                                                   Map whereJson,
                                                   List<OrderBy> orderBys,
                                                   Integer skip,
                                                   Integer limit,
                                                   List<String> keys,
                                                   List<String> groupBy,
                                                   Map<String, Object> optionalParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        select_keys(keys, optionalParam, searchSourceBuilder);
        whereJson(whereJson, optionalParam, searchSourceBuilder);
        group_by(indexName, groupBy, optionalParam, searchSourceBuilder);
        order_by(indexName, orderBys, optionalParam, searchSourceBuilder);
        skipLimit(skip, limit, searchSourceBuilder);

        if (log.isDebugEnabled()) {
            log.info("\n███Elasticsearch 【DSL Query】 \nGET /{}/_search\n{}\n", indexName, JSON.toJSONString(JSON.parseObject(searchSourceBuilder.toString()), true));
        }

        // from ${indexName}
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    private void group_by(String indexName, List<String> groupByKeys, Map<String, Object> optionalParam, SearchSourceBuilder searchSourceBuilder) {
        if (isEmpty(groupByKeys)) {
            return;
        }

        Map<String, Map<String, Object>> mapping = getMapping(indexName, client);

        HashMap<String, String> groupByKeyMap = new HashMap<>();
        for (String groupByKey : groupByKeys) {
            boolean isKeyWord = EsSearchUtil.isKeyword(mapping, groupByKey);
            TermsAggregationBuilder terms = AggregationBuilders.terms(groupByKey).field(keyWord(groupByKey, isKeyWord));
            searchSourceBuilder.aggregation(terms);
            groupByKeyMap.put(groupByKey, keyWord(groupByKey, isKeyWord));
        }

        optionalParam.put("groupByKeyMap", groupByKeyMap); // groupByKeyMap格式：{"name":"name.keyword","age":"age"}
    }

    private void order_by(String indexName, List<OrderBy> orderBys, Map<String, Object> optionalParam, SearchSourceBuilder searchSourceBuilder) {
        if (isEmpty(orderBys)) {
            return;
        }

        // 先要查询当前index所有字段的类型。然后再根据字段类型，来判断是否调用keyWord()方法。
        Map<String, Map<String, Object>> mapping = getMapping(indexName, client);
        for (OrderBy orderBy : orderBys) {
            String key = orderBy.getKey();
            String type = MapUtil.getStr(mapping.get(key), "type");
            if ("text".equals(type)) {
                key = keyWord(key, "true");
            }
            SortOrder order = "DESC".equals(orderBy.getOrder()) ? SortOrder.DESC : SortOrder.ASC;
            searchSourceBuilder.sort(new FieldSortBuilder(key).order(order));
        }
    }

    private void skipLimit(Integer skip, Integer limit, SearchSourceBuilder searchSourceBuilder) {
        if (skip != null && skip > 0) {
            searchSourceBuilder.from(skip);
        }
        // limit 为0，则说明不需要分页
        if (limit != null && limit > 0 && limit <= 1000) {
            searchSourceBuilder.size(limit);
        }
        // 最大1000
        if (limit != null && limit > 1000) {
            searchSourceBuilder.size(1000);
        }
        // 默认100
        int size = searchSourceBuilder.size();
        if (size == -1) {
            searchSourceBuilder.size(100);
        }
    }

    private void select_keys(List<String> keys, Map<String, Object> optionalParam, SearchSourceBuilder searchSourceBuilder) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }

        // 是否需要查询count.如果需要查询count，则不返回源文档
        Boolean hasSelectCount = MapUtil.getBool(optionalParam, "hasSelectCount");
        if (hasSelectCount) {
            searchSourceBuilder.fetchSource(false);
        }

        String[] includeFields = keys.toArray(new String[]{});
        searchSourceBuilder.fetchSource(includeFields, excludesFields);
    }

    private void whereJson(Map<String, Object> whereJson,
                           Map<String, Object> optionalParam,
                           SearchSourceBuilder searchSourceBuilder) {

        // <if test="whereJson != null and whereJson instanceof java.util.Map">
        if (isEmpty(whereJson)) {
            return;
        }


        // <foreach collection="whereJson.entrySet()" index="key" item="value">
        whereJson.forEach((key, value) -> {
            // <include refid="where_condition"/>
            where_condition(whereJson, optionalParam, searchSourceBuilder);

            // <when test="key == '$and' and value instanceof java.util.List">
            //     <foreach collection="value" item="and_element" open="" close="" separator="" index="">
            //         <!--   and_element = {"tags":"北京"}    -->
            //         <foreach collection="and_element.entrySet()" index="key" item="value">
            //             <include refid="where_condition"/>
            //         </foreach>
            //     </foreach>
            // </when>
            if ("$and".equals(key) && value instanceof List) {
                ((List) value).forEach(mapElement -> {
                    ((Map) mapElement).forEach((mapElementKey, mapElementValue) -> {
                        if ("$or".equals(mapElementKey)) {
                            or_query_condition((List<Map>) mapElementValue, searchSourceBuilder, optionalParam);
                        } else {
                            where_condition((Map<String, Object>) mapElement, optionalParam, searchSourceBuilder);
                        }
                    });
                });
            }
            // <when test="key == '$or' and value instanceof java.util.List">
            //     AND (
            //     <trim prefixOverrides="OR">
            //             <foreach collection="value" item="or_element" open="" close="" separator="" index="">
            //                 <!--   or_element = {"tags":"北京"}    -->
            //                 <foreach collection="or_element.entrySet()" index="key" item="value">
            //                     OR <trim prefixOverrides="AND|OR"><include refid="where_condition"/></trim>
            //                 </foreach>
            //             </foreach>
            //         </trim>
            //         )
            // </when>
            if ("$or".equals(key) && value instanceof List) {
                or_query_condition((List<Map>) value, searchSourceBuilder, optionalParam);
            }
        }); //whereJson forEach end

    }

    /**
     * <pre>
     * 处理or查询条件
     * or的查询条件格式：{"$or":[{"title":"notExistValue"},{"tags":"生活"}]}
     * </pre>
     *
     * @param orQueryContionList  格式：[{"title":"notExistValue"},{"tags":"生活"}]
     * @param searchSourceBuilder
     * @param optionalParam
     */
    private void or_query_condition(List<Map> orQueryContionList, SearchSourceBuilder searchSourceBuilder, Map<String, Object> optionalParam) {
        optionalParam.put("is_and_query", false);
        QueryBuilder existQuery = searchSourceBuilder.query();
        if (existQuery != null && !(existQuery instanceof BooleanQueryBuilder)) {
            // 在处理or查询之前，如果已经存在了一个查询，并且不是bool查询：则使用bool.must查询包装一下。
            // 是为了解决：对于查询：{where={"$and":[{"readCount":56},{"$or":[{"title":"notExistValue"},{"tags":"生活"}]}]}} 有问题。构造查询时，会把三个条件都当成should查询
            searchSourceBuilder.query(boolQuery().must(existQuery));
        }
        orQueryContionList.forEach(listItemMap -> where_condition(listItemMap, optionalParam, searchSourceBuilder));
        optionalParam.remove("is_and_query");
    }

    private void where_condition(Map<String, Object> whereJson,
                                 Map<String, Object> optionalParam, SearchSourceBuilder searchSourceBuilder) {

        if (whereJson == null || whereJson.isEmpty()) {
            return;
        }


        whereJson.forEach((key, value) -> {

            // 如果objectId传的参数为String类型，就转成Long类型。
            // 原因：Long类型会去es查询objectId字段；String会去es查询objectId.keyword，此时是查询不到数据的。这个操作是为了兼容objectId的参数类型。
            if (Objects.equals(Constants.KEY_NAME_OBJECT_ID, key) && value instanceof String) {
                value = Long.valueOf((String) value);
            }

            QueryBuilder existQuery = searchSourceBuilder.query(); // 在处理当前查询条件之前，已经现存的query

            // 当前是否是 and 查询   默认为and查询
            Boolean isAndQuery = MapUtil.getBool(optionalParam, "is_and_query");
            if (isAndQuery == null) {
                isAndQuery = true;
            }

            // <if test="value instanceof java.lang.String or value instanceof java.lang.Integer or value instanceof java.lang.Long">
            //     <include refid="and_or_choose"/>
            //     ${key} = #{value}
            // </if>
            if (value instanceof String || value instanceof Integer || value instanceof Long) {
                TermQueryBuilder currentQuery = termQuery(keyWord(key, value), value);
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }

            if (!(value instanceof Map)) {
                return;
            }
            Map valueMap = (Map) value;

            // <if test="value instanceof java.util.Map and value.containsKey('$in')">
            //     <include refid="and_or_choose"/> ${key} in
            //     <foreach collection="value.get('$in')" index="i" open="(" close=")" item="in_item" separator=",">
            //         #{in_item}
            //     </foreach>
            // </if>
            if (valueMap.get("$in") instanceof Collection) {
                Object[] insValue = ((Collection) valueMap.get("$in")).toArray();
                TermsQueryBuilder currentQuery = termsQuery(keyWord(key, insValue), insValue);
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$nin')">
            //     <include refid="and_or_choose"/> ${key} not in
            //     <foreach collection="value.get('$nin')" index="i" open="(" close=")" item="in_item" separator=",">
            //         #{in_item}
            //     </foreach>
            // </if>
            if (valueMap.get("$nin") instanceof Collection) {
                Object[] ninsValue = ((Collection) valueMap.get("$nin")).toArray();
                BoolQueryBuilder currentQuery = boolQuery().mustNot(termsQuery(keyWord(key, ninsValue), ninsValue));
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }

            // <if test="value instanceof java.util.Map and value.containsKey('$ne')">
            //     <include refid="and_or_choose"/> ${key} != '${value.get("$ne")}'
            // </if>
            if (valueMap.containsKey("$ne")) {
                Object neValue = valueMap.get("$ne");
                QueryBuilder currentQuery = boolQuery().mustNot(termQuery(keyWord(key, neValue), neValue));
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$regex')">
            //     <include refid="and_or_choose"/>
            //     (
            //     <trim prefixOverrides="OR">
            //         <choose>
            //             <when test="value.get('$regex').startsWith('^') and !value.get('$regex').endsWith('$')">
            //                 OR ${key} LIKE '${value.get("$regex").replace(".*", "%").replace("^", "")}'
            //             </when>
            //             <when test="!value.get('$regex').startsWith('^') and value.get('$regex').endsWith('$')">
            //                 OR ${key} LIKE '%${value.get("$regex").replace(".*", "%").replace("$", "")}'
            //             </when>
            //             <when test="value.get('$regex').startsWith('^') and value.get('$regex').endsWith('$')">
            //                 OR ${key} LIKE '${value.get("$regex").replace(".*", "%").replace("^", "").replace("$", "")}'
            //             </when>
            //             <otherwise>
            //                 OR ${key} RLIKE '${value.get("$regex")}'
            //             </otherwise>
            //         </choose>
            //     </trim>
            //     )
            // </if>
            if (valueMap.get("$regex") instanceof String) {
                String paramRegexValue = valueMap.get("$regex").toString();
                String wildcard = paramRegexValue.replaceAll("[\\^\\$]", "")
                                                 .replace(".*", "*")
                                                 .replace("**", "*");

                WildcardQueryBuilder currentQuery = wildcardQuery(keyWord(key, wildcard), wildcard);
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);

                // RLIKE 使用的是 regexp 查询   ;  regexp   正则格式为：  .*xxxxx.*
                //  LIKE 使用的是 wildcard 查询 ;  wildcard 匹配格式为：   *xxxxx*         查询结果都是一样的。
                // todo:但是不知道底层性能/实现有什么不一样。另外 regexp 查询可以设置更多参数。
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$gt')">
            //     <include refid="and_or_choose"/> ${key} > '${value.get("$gt")}'
            // </if>
            if (valueMap.containsKey("$gt")) {
                RangeQueryBuilder gt = rangeQuery(key).gt(valueMap.get("$gt"));
                and_or_query_choose(gt, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$gte')">
            //     <include refid="and_or_choose"/> ${key} >= '${value.get("$gte")}'
            // </if>
            if (valueMap.containsKey("$gte")) {
                RangeQueryBuilder gte = rangeQuery(key).gte(valueMap.get("$gte"));
                and_or_query_choose(gte, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$lt')">
            //     <include refid="and_or_choose"/> ${key} &lt; '${value.get("$lt")}'
            // </if>
            if (valueMap.containsKey("$lt")) {
                RangeQueryBuilder lt = rangeQuery(key).lt(valueMap.get("$lt"));
                and_or_query_choose(lt, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$lte')">
            //     <include refid="and_or_choose"/> ${key} &lt;= '${value.get("$lte")}'
            // </if>
            if (valueMap.containsKey("$lte")) {
                RangeQueryBuilder lte = rangeQuery(key).lte(valueMap.get("$lte"));
                and_or_query_choose(lte, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.get('__type') == 'Pointer'">
            //     <include refid="and_or_choose"/>
            //     <if test="value.remove('notInQueryPlaceholder') != null"> NOT </if> server_data -> '${key}' @> '${value}'::jsonb
            // </if>
            if (Objects.equals(valueMap.get("__type"), "Pointer")) {
                // todo:    pointer     这个目前没有使用到。
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$size')">
            //     <include refid="and_or_choose"/> length(${key}) = ${value.get("$size")}
            // </if>
            if (valueMap.containsKey("$size")) {
                // todo:      查询数组个数              不知道这样写有没有bug ???
                String queryString = String.format("length(%s)=%s", key, ((Integer) valueMap.get("$size")) + 1);
                QueryStringQueryBuilder currentQuery = queryStringQuery(queryString);
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$all')">
            //     <include refid="and_or_choose"/>
            //     <if test="key != 'objectId'"> server_data -> '${key}' @> '${value.get("$all")}'::jsonb </if>
            //     <if test="key == 'objectId'">
            //         <foreach collection="value.get('$all')" separator="and" open="(" close=")" item="v">
            //             objectId = #{v}
            //         </foreach>
            //     </if>
            // </if>
            // 参考：https://www.cnblogs.com/dongruiha/p/12201195.html
            if (valueMap.get("$all") instanceof Collection) {
                List $all = (List) valueMap.get("$all");
                array_$all_query(searchSourceBuilder, key, existQuery, isAndQuery, $all);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$exists')">
            //     <include refid="and_or_choose"/> <if test="!value.get('$exists')"> NOT </if> QUERY('_exists_:"${key}"')
            // </if>
            if ((valueMap.get("$exists") instanceof Boolean)) {
                if ((Boolean) valueMap.get("$exists")) {
                    ExistsQueryBuilder currentQuery = existsQuery(key);
                    and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
                } else {
                    BoolQueryBuilder currentQuery = boolQuery().mustNot(existsQuery(key));
                    and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
                }
            }

            if (valueMap.get("$isNull") instanceof Boolean) {
                if ((Boolean) valueMap.get("$isNull")) {
                    BoolQueryBuilder currentQuery = boolQuery().mustNot(existsQuery(key));
                    and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
                } else {
                    ExistsQueryBuilder currentQuery = existsQuery(key);
                    and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
                }
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$fullTextMatch')">
            //     <include refid="and_or_choose"/> MATCH(${key}, '${value.get("$fullTextMatch")}')
            // </if>
            if (valueMap.containsKey("$fullTextMatch")) {
                MatchQueryBuilder currentQuery = matchQuery(key, valueMap.get("$fullTextMatch"));
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }
            // <if test="value instanceof java.util.Map and value.containsKey('$fullTextQuery')">
            //     <include refid="and_or_choose"/> QUERY('${value.get("$fullTextQuery")}')
            // </if>
            if (valueMap.get("$fullTextQuery") instanceof String) {
                QueryStringQueryBuilder currentQuery = queryStringQuery(valueMap.get("$fullTextQuery").toString());
                and_or_query_choose(currentQuery, isAndQuery, searchSourceBuilder);
            }
        }); // whereJson.forEach end
    }

    private void array_$all_query(SearchSourceBuilder searchSourceBuilder, String key, QueryBuilder existQuery, Boolean isAndQuery, List $allQueryValues) {
        if ($allQueryValues.size() <= 1) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery((keyWord(key, $allQueryValues.get(0))), $allQueryValues.get(0));
            and_or_query_choose(termQueryBuilder, isAndQuery, searchSourceBuilder);
        } else {
            BoolQueryBuilder boolQuery = boolQuery();
            for (Object item : $allQueryValues) {
                boolQuery.filter(termQuery(keyWord(key, item), item));
            }
            and_or_query_choose(boolQuery, isAndQuery, searchSourceBuilder);
        }
    }
}
