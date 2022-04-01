package com.liuxianfa.junit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.support.ApiController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.core.map.MapUtil;

@Controller
@RequestMapping("/api")
public class IndexController extends ApiController {

    @Override
    protected void initApiConfig(ApiConfig apiConfig) {

        // 添加秘钥配置，map中存放秘钥信息，key对应appKey，value对应secret
        apiConfig.addAppSecret(MapUtil.of("test", "123456"));

        apiConfig.setTimeoutSeconds(600000);
        apiConfig.setShowDoc(true);
        apiConfig.openAppMode();

        apiConfig.setJsonResultSerializer(obj -> JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty,
                                                                   SerializerFeature.DisableCircularReferenceDetect));
    }
}