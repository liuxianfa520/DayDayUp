import com.alibaba.fastjson.JSON;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.SneakyThrows;

public class ElasticsearchClientTest {

    static ElasticsearchClient client;

    public static void main(String[] args) {
        setUp();
        index();
        get();
        search();

        upsert();
    }

    public static void setUp() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));
        RestClient restClient = RestClient.builder(new HttpHost("192.168.100.72", 9200))
                                    .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider))
                                    .build();
        client = new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

    static String indexName = "user";
    static String id = "1";
    static HashMap<String, String> doc = MapUtil.of("name", "zhangsan");

    @lombok.SneakyThrows
    public static void index() {
        IndexResponse index = client.index(builder -> builder.index(indexName).id(id).document(doc));
        System.err.println(JSON.toJSONString(index.result(), true));
        System.err.println(index.id());
    }

    @lombok.SneakyThrows
    public static void get() {
        GetResponse<HashMap> r = client.get(builder -> builder.index(indexName).id(id), HashMap.class);
        System.err.println(JSON.toJSONString(r, true));
        System.err.println(r.index());
        System.err.println(r.id());
        System.err.println(JSONUtil.toJsonPrettyStr(r.source()));
    }


    @SneakyThrows
    public static void search() {
        SearchResponse<HashMap> r = client.search(builder -> builder.index(indexName).query(query -> query.match(e -> e.field("name").query("zhangsan"))), HashMap.class);
        System.err.println(JSON.toJSONString(r, true));
        System.err.println(JSON.toJSONString(r.hits(), true));
    }

    @SneakyThrows
    public static void upsert() {
        Map<Object, Object> doc = MapUtil.builder().put("name", "张三").put("age", "18").build();
        UpdateResponse<Map> update = client.update(builder -> builder.id("555").index(indexName).upsert(doc).doc(doc), Map.class);
        System.err.println("修改还是新增?" + update.result());
        System.err.println(JSON.toJSONString(update, true));

        GetResponse<Map> mapGetResponse = client.get(builder -> builder.index(indexName).id(id), Map.class);
        System.err.println(JSON.toJSONString(mapGetResponse.source(), true));
    }
}