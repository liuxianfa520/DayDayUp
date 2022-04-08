package com.pandora.storage.es.api;


import com.pandora.storage.es.entity.DataStorage;

//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/4/17 18:06
 */
//@FeignClient(name = "mehe-storage-es-service", contextId = "esStorageFeignClient")
public interface EsStorageFeignClient {

    @GetMapping("test/{str}")
    String test(@PathVariable("str") String str);

    /**
     * @param indexName
     * @param id
     * @param source
     * @return data字段为：索引成功的文档id；其值等于 参数id
     */
    @PostMapping("index/{indexName}/{id}")
    String index(@PathVariable("indexName") String indexName,
                 @PathVariable("id") String id,
                 @RequestBody String source);

    /**
     * 批量索引文档，
     *
     * @param indexName
     * @param sources   多个文档内容，需要包含系统保留字段
     * @return
     */
    @PostMapping("index/{indexName}")
    List<String> indexBatch(@PathVariable("indexName") String indexName,
                            @RequestBody String sources);

    @GetMapping("get/{indexName}")
    List<DataStorage> get(@PathVariable("indexName") String indexName);

    @GetMapping("get/id/{objectId}")
    DataStorage get(@PathVariable("objectId") Long objectId);

    @GetMapping("get/{indexName}/{id}")
    DataStorage get(@PathVariable("indexName") String indexName,
                    @PathVariable("id") String id);

    @GetMapping("exist/{indexName}/{id}")
    boolean exist(@PathVariable("indexName") String indexName,
                  @PathVariable("id") String id);

    /**
     * @param indexName
     * @param id
     * @return 被删除的文档id；其值应该等于 id参数
     */
    @PostMapping("delete/{indexName}/{id}")
    String delete(@PathVariable("indexName") String indexName,
                  @PathVariable("id") String id);

    /**
     * @param indexName
     * @param id
     * @param dataStorageJsonString
     * @param fetchSource
     * @return data字段为 {@link DataStorage} 类型；如果 fetchSource=true，则data就是修改后文档数据。
     */
    @PostMapping("update/{indexName}/{id}")
    DataStorage update(@PathVariable("indexName") String indexName,
                       @PathVariable("id") String id,
                       @RequestBody String dataStorageJsonString,
                       @RequestParam(value = "fetchSource", defaultValue = "false") boolean fetchSource);

    /**
     * @param indexName
     * @param id
     * @param upsertContent
     * @param fetchSource
     * @return data字段为 {@link DataStorage} 类型；如果 fetchSource=true，则data就是修改后文档数据。
     */
    @PostMapping("upsert/{indexName}/{id}")
    DataStorage upsert(@PathVariable("indexName") String indexName,
                       @PathVariable("id") String id,
                       @RequestBody String upsertContent,
                       @RequestParam(value = "fetchSource", defaultValue = "false") boolean fetchSource);

    @PostMapping("query")
    List<DataStorage> query(@RequestBody Map<String, String> queryMap);

}
