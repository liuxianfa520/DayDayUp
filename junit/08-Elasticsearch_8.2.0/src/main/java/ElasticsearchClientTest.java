import com.alibaba.fastjson.JSON;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.HashMap;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
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

    @lombok.SneakyThrows
    public static void index() {
        HashMap<String, String> of = MapUtil.of("name", "zhangsan");
        IndexResponse index = client.index(builder -> builder.index(indexName).id(id).document(of));
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


}

























