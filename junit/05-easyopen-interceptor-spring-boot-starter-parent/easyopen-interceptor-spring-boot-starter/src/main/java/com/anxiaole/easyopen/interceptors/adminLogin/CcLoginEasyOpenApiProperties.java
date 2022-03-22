package com.anxiaole.easyopen.interceptors.adminLogin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 文档地址:https://cc-test310.newtamp.cn/gwy/doc#404
 * <p>
 * 顾问云-获取登录用户信息 apiName: gwy.base.userInfo
 *
 * @author LiuXianfa
 * 
 * @date 2/25 22:27
 */
@Data
@ConfigurationProperties(prefix = "cc.gwy.easyopen.url")
public class CcLoginEasyOpenApiProperties {

    /**
     * 顾问云-获取登录用户信息 apiName: gwy.base.userInfo
     * <p>
     * 只有cc-admin项目接口修改,这个值才会修改.(用户无法通过配置文件修改.)
     */
    public static final String apiName = "gwy.base.userInfo";

    /**
     * 默认的cc-admin项目的easyopen网关url
     * <p>
     * 默认使用的是svc名称: 由于项目使用k8s部署,在pod之间的http请求,可以直接使用[svc名称]和[pod内部端口]即可. 详细去请教运维.
     */
    private static final String DEFAULT_API_URL = "http://configcenter:8080/gwy";
    private static final String DEFAULT_SECRET = "123456";
    private static final String DEFAULT_APP_KEY = "test";


    String apiUrl = DEFAULT_API_URL;

    String appKey = DEFAULT_APP_KEY;

    String secret = DEFAULT_SECRET;
}
