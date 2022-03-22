package com.anxiaole.easyopen;

import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.interceptor.ApiInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * <pre>
 * 在 {@link com.gitee.easyopen.support.ApiController#onApplicationEvent} 执行完毕之后,给easyopen添加拦截器
 * </pre>
 *
 * @author LiuXianfa
 * 
 * @date 11/25 16:10
 */
public class EasyOpenInterceptorsApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    List<ApiInterceptor> apiInterceptor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApiConfig apiConfig = ApiContext.getApiConfig();
        // monitorInterceptor 需要放在第一个位置,其余的会根据spring排序规则进行排序
        ApiInterceptor monitorInterceptor = apiConfig.getMonitorInerceptor();
        // 把用户注册的拦截器和spring容器中的拦截器,根据class去重.
        // (用户注册的拦截器放在spring容器中,并且也使用apiConfig.setInterceptors(new ApiInterceptor[] { new XxxInterceptor() });注册拦截器时,拦截器会重复.)
        Set<Class> classSet = apiInterceptor.stream().map(ApiInterceptor::getClass).collect(Collectors.toSet());
        apiInterceptor.addAll(Arrays.stream(apiConfig.getInterceptors())
                                    .filter(interceptor -> (interceptor != monitorInterceptor) && classSet.add(interceptor.getClass()))
                                    .collect(toList()));

        if (monitorInterceptor != null) {
            apiInterceptor.add(0, monitorInterceptor);// monitorInterceptor 放在第一个位置.
        }
        apiConfig.setInterceptors(apiInterceptor.toArray(apiConfig.getInterceptors()));


        // log
        logger.info(String.format("--EasyOpen启用的Interceptors为:[%s]", apiInterceptor.stream()
                                                                                   .map(interceptor -> interceptor.getClass().getSimpleName())
                                                                                   .collect(Collectors.joining(","))));
    }
}
