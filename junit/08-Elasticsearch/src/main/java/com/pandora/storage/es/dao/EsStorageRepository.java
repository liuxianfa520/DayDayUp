package com.pandora.storage.es.dao;

import com.alibaba.fastjson.JSON;
import com.pandora.storage.es.entity.DataStorage;
import com.pandora.storage.es.exception.MeheException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pandora.storage.es.dao.EsSearchUtil.search;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/20 14:14
 */
@Repository
public class EsStorageRepository {
    private static final Logger logger = LoggerFactory.getLogger(EsStorageRepository.class);

    @Autowired
    private RestHighLevelClient client;


    // -------------------- index --------------------------------

    public String index(String indexName, String id, DataStorage dataStorage) {
        // 如果map中包含serverData，就把serverData对应的json字符串转成key-value保存。
        if (dataStorage.containsKey("serverData")) {
            dataStorage.transferAndRemoveServerData();
        }
        return index(indexName, id, JSON.toJSONString(dataStorage));
    }

    public String index(String indexName, String id, String source) {
        IndexRequest request = new IndexRequest(indexName).id(id).source(source, XContentType.JSON);

        try {
            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                logger.debug("Handle (if needed) the case where the document was created for the first time");
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                logger.debug("Handle (if needed) the case where the document was rewritten as it was already existing");
            }
            return indexResponse.getId();
        } catch (IOException e) {
            logger.error("Index doc 异常.", e);
            throw new MeheException("索引文档失败。异常信息:" + e.getMessage());
        }
    }

    // 根据indexName查询所有
    public List<DataStorage> get(String indexName) {
        return getPage(indexName, -1, -1);
    }

    /**
     * @param indexName
     * @param from      从0开始
     * @param size
     * @return
     */
    public List<DataStorage> getPage(String indexName, int from, int size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .timeout(new TimeValue(30, TimeUnit.SECONDS));
        if (from != -1) {
            searchSourceBuilder.from(from);
        }
        if (size != -1) {
            searchSourceBuilder.size(size);
        }

        SearchRequest searchRequest = new SearchRequest(indexName).source(searchSourceBuilder);
        return search(indexName, searchRequest, client);
    }

    /**
     * 只通过id查询es doc
     *
     * @param objectId
     * @return
     */
    public DataStorage getByObjectId(Long objectId) {
        SearchSourceBuilder searchSource = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery("objectId", Long.valueOf(objectId)))
                .size(1);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSource);
        List<DataStorage> result = search("", searchRequest, client);
        if (result.isEmpty()) {
            throw new MeheException("获取文档异常。当前id的文档不存在！");
        }
        return result.get(0);
    }

    public DataStorage get(String indexName, String id) {
        GetRequest getRequest = new GetRequest(indexName, id);

        try {
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            boolean exists = getResponse.isExists();
            logger.info("获取文档，是否存在：{}", exists);
            if (exists) {
                return JSON.parseObject(getResponse.getSourceAsString(), DataStorage.class);
            } else {
                throw new MeheException("获取文档异常。异常信息：文档不存在。");
            }
        } catch (IOException e) {
            String s = "Get doc 异常。";
            logger.error(s, e);
            throw new MeheException(s + "异常信息：" + e.getMessage());
        }
    }

    public boolean exist(String indexName, String id) {
        GetRequest getRequest = new GetRequest(indexName, id);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        getRequest.fetchSourceContext(new FetchSourceContext(false));

        try {
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            String s = "Doc exists 异常。";
            logger.error(s, e);
            throw new MeheException(s + "异常信息：" + e.getMessage());
        }
    }

    public String delete(String indexName, String id) {
        DeleteRequest request = new DeleteRequest(indexName, id);
        try {
            DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                // Do something if the document to be deleted was not found
            }
            return deleteResponse.getId();
        } catch (IOException e) {
            String s = "Delete Doc 异常。";
            logger.error(s, e);
            throw new MeheException(s + "异常信息：" + e.getMessage());
        }
    }


    /**
     * fetchSource為true则返回修改后的数据，否则返回null
     *
     * @param indexName
     * @param id
     * @param content
     * @param upsertContent
     * @param fetchSource
     * @return
     */
    public DataStorage update(String indexName, String id, DataStorage content, DataStorage upsertContent, Boolean fetchSource) {
        UpdateRequest request = new UpdateRequest(indexName, id);

        // doc （content是Map，es会把map中的date类型，序列化成时间戳）
        if (content != null && !content.isEmpty()) {
            request.doc(content, XContentType.JSON);
        }

        // If the document does not already exist, it is possible to define some content that will be inserted as a new document using the upsert method:
        if (upsertContent != null && !upsertContent.isEmpty()) {
            request.upsert(upsertContent, XContentType.JSON);
        }

        if (fetchSource != null) {
            request.fetchSource(fetchSource); //Enable source retrieval, disabled by default
        }

        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);

            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                logger.debug("Handle the case where the document was created for the first time (upsert)");
            } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                logger.debug("Handle the case where the document was updated");
            } else if (response.getResult() == DocWriteResponse.Result.DELETED) {
                logger.debug("Handle the case where the document was deleted");
            } else if (response.getResult() == DocWriteResponse.Result.NOOP) {
                logger.debug("Handle the case where the document was not impacted by the update, ie no operation (noop) was executed on the document");
            }

            if (fetchSource != null && !fetchSource) {
                return null;
            }

            GetResult result = response.getGetResult();
            if (result.isExists()) {
                return JSON.parseObject(result.sourceAsString(), DataStorage.class);
            } else {
                logger.warn("Handle the scenario where the source of the document is not present in the response (this is the case by default)");
            }
        } catch (IOException e) {
            String s = "Update Doc 异常。";
            logger.error(s, e);
            throw new MeheException(s + "异常信息：" + e.getMessage());
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                String msg = "需要修改的文档数据不存在！objectId=" + id;
                logger.error(msg, e);
                throw new MeheException(msg);
            }
            throw new MeheException("修改es异常！", e);
        }
        return null;
    }


    // -------------------- search --------------------------------

    public long count() {
        return 0;
    }


    /**
     * @param indexName
     * @return true:所有es节点是否已确认请求
     */
    public boolean deleteIndex(String indexName) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            // Indicates whether all of the nodes have acknowledged the request
            boolean acknowledged = deleteIndexResponse.isAcknowledged();
            logger.info("删除索引：【{}】——Indicates whether all of the nodes have acknowledged the request.", acknowledged);
            return acknowledged;
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
                // Do something if the index to be deleted was not found
                throw new MeheException(String.format("delete index 异常。%s 不存在索引 ", indexName));
            }
        } catch (IOException e) {
            String s = "Delete index 异常。";
            logger.error(s, e);
            throw new MeheException(s + "异常信息：" + e.getMessage());
        }

        return false;
    }
}