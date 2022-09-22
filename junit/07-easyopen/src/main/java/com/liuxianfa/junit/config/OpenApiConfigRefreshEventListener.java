package com.liuxianfa.junit.config;

import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.doc.ApiServiceDocCreator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * openapi配置刷新监听器
 *
 * <pre>
 * 使用说明:当使用SpringCloud并且配置中心使用nacos时 (或者其他可以感知配置变化的配置中心)
 * 可以通过实现 org.springframework.cloud.endpoint.event.RefreshEventListener 来监听application.yml或application.properties 配置文件的变化,
 * 动态修改程序中的配置.
 *
 * 这里是使用openapi的文档开关来举例.不过,由于当前这个项目没有使用SpringCloud和nacos,所以无法演示.
 * </pre>
 */
@Component
@Slf4j
public class OpenApiConfigRefreshEventListener extends RefreshEventListener {
    private ContextRefresher refresh;
    @Autowired
    private ApplicationContext applicationContext;

    public OpenApiConfigRefreshEventListener(ContextRefresher refresh) {
        super(refresh);
        this.refresh = refresh;
    }

    @Override
    public void handle(RefreshEvent event) {
        log.debug("Event received " + event.getEventDesc());
        Set<String> keys = this.refresh.refresh();
        log.info("Refresh keys changed: " + keys);

        if (CollUtil.isEmpty(keys)) {
            return;
        }

        // 在配置文件中的配置项:openapi的文档密码.可以在配置文件中把此项改成 '' ,也就是改成空字符串,就不使用文档密码了.
        String docPasswordKey = "openapi.api-config.docPassword";
        if (keys.contains(docPasswordKey)) {
            ApiContext.getApiConfig().setDocPassword(SpringUtil.getProperty(docPasswordKey));
        }

        // 在配置文件中的配置项:是否开启openapi文档
        String showDocKey = "openapi.api-config.showDoc";
        if (keys.contains(showDocKey)) {
            ApiContext.getApiConfig().setShowDoc(Boolean.valueOf(SpringUtil.getProperty(showDocKey)));
            if (ApiContext.getApiConfig().getShowDoc()) {
                log.info("生成接口文档");
                new ApiServiceDocCreator(ApiContext.getApiConfig().getDefaultVersion(), applicationContext)
                        .create();
            }
        }
    }
}
