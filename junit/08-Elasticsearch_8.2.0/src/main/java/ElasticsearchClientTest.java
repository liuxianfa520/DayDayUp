import com.alibaba.fastjson.JSON;

import org.apache.http.HttpHost;
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
        // Create the low-level client
        RestClient restClient = RestClient.builder(new HttpHost("127.0.0.1", 9200)).build();


        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        client = new ElasticsearchClient(transport);
    }

    static String indexName = "user";
    static String id = "1";
    static HashMap<String, String> doc = MapUtil.of("name", "zhangsan");

    @lombok.SneakyThrows
    public static void index() {
        IndexResponse index = client.index(builder -> builder.index(indexName).id(id).document(doc));
        System.out.println(JSON.toJSONString(index.result(), true));
        System.out.println(index.id());
    }

    @lombok.SneakyThrows
    public static void get() {
        GetResponse<HashMap> r = client.get(builder -> builder.index(indexName).id(id), HashMap.class);
        System.out.println(JSON.toJSONString(r, true));
        System.out.println(r.index());
        System.out.println(r.id());
        System.out.println(JSONUtil.toJsonPrettyStr(r.source()));
    }


    @SneakyThrows
    public static void search() {
        SearchResponse<HashMap> r = client.search(builder -> builder.index(indexName).query(query -> query.match(e -> e.field("name").query("zhangsan"))), HashMap.class);
        System.out.println(JSON.toJSONString(r, true));
        System.out.println(JSON.toJSONString(r.hits(), true));
    }

    @SneakyThrows
    public static void upsert() {
        Map<Object, Object> build = MapUtil.builder().put("name", "张三").put("age", "18").build();
        UpdateResponse<Map> update = client.update(builder -> builder.id(id).index(indexName).upsert(build), Map.class);
        System.out.println(JSON.toJSONString(update, true));
        System.out.println(JSON.toJSONString(update.result(), true));

        GetResponse<Map> mapGetResponse = client.get(builder -> builder.index(indexName).id(id), Map.class);
        System.out.println(JSON.toJSONString(mapGetResponse.source(), true));
    }


}

























