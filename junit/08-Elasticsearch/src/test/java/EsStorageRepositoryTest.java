import com.pandora.storage.es.MeheEsApplication;
import com.pandora.storage.es.dao.EsStorageRepository;
import com.pandora.storage.es.entity.DataStorage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/4/6 11:05
 */
@SpringBootTest(classes = MeheEsApplication.class)
@RunWith(value = SpringRunner.class)
public class EsStorageRepositoryTest {

    @Autowired
    EsStorageRepository esStorageRepository;

    @Test
    public void index() {
        Map<Object, Object> build = MapUtil.builder()
                                           .put("name", "zhangsan")
                                           .put("age", "16")
                                           .build();
        String objectId = UUID.fastUUID().toString(true);

        System.out.println("id: "+esStorageRepository.index("test", objectId, JSONUtil.toJsonStr(build)));
    }

    @Test
    public void get() {
        List<DataStorage> test = esStorageRepository.get("test");
        System.out.println(JSONUtil.toJsonPrettyStr(test));
    }

    @Test
    public void getById() {
        DataStorage test = esStorageRepository.get("test", "899a8475bfcb42569ce3549dad5411d7");
        System.out.println(JSONUtil.toJsonPrettyStr(test));
    }
}