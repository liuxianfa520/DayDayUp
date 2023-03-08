//package com.liuxianfa.junit.nacos.listener;
//
//import com.alibaba.cloud.nacos.NacosConfigManager;
//import com.alibaba.cloud.nacos.NacosConfigProperties;
//import com.alibaba.nacos.api.config.listener.Listener;
//
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executor;
//import java.util.stream.Collectors;
//
//import javax.annotation.Resource;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * <pre>
// * 白名单监听器
// * 监听nacos的 {@link WhiteListNacosListener#WHITELIST_DATA_ID} 配置文件.
// * 并把白名单配置保存到redis中.
// * </pre>
// */
//@RefreshScope
//@Configuration
//@Slf4j
//public class WhiteListNacosListener implements InitializingBean {
//
//    /**
//     * 白名单配置,在nacos中配置文件的dataId
//     */
//    private static final String WHITELIST_DATA_ID = "whitelist-config.yaml";
//
//    @Resource
//    private NacosConfigManager nacosConfigManager;
//
//    @Resource
//    private NacosConfigProperties configProperties;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        nacosConfigManager.getConfigService()
//                          .addListener(WHITELIST_DATA_ID, configProperties.getGroup(),
//                                       new Listener() {
//                                           @Override
//                                           public Executor getExecutor() {
//                                               return null;
//                                           }
//
//                                           @Override
//                                           public void receiveConfigInfo(String config) {
//                                               log.info("nacos白名单配置变更.配置值:{}", config);
//                                               WhiteListNacosListener.this.receiveConfigInfo(config);
//                                               log.info("nacos白名单配置变更成功.");
//                                           }
//                                       });
//    }
//
//    private void receiveConfigInfo(String config) {
//        try {
//            WhiteListConfig whiteListConfig = JSONUtil.toBean(config, WhiteListConfig.class);
//            for (WhiteList whiteList : whiteListConfig.whitelist) {
//                saveWhitelistToRedis(whiteList);
//            }
//        } catch (Exception e) {
//            log.error("nacos白名单配置变更异常!", e);
//        }
//    }
//
//    private void saveWhitelistToRedis(WhiteList whiteList) {
//        RedisTemplate<String, String> redisTemplate = null;
//        try {
//            RedisConfig redis = whiteList.redis;
//            String whitelistenablekey = redis.whitelistenablekey;
//
//            // 构建redisTemplate
//            redisTemplate = buildRedisTemplate(redis.hostname, redis.port, redis.database, redis.password);
//
//            // 先保存  白名单是否启用        kv数据结构:whitelistenablekey->enable
//            redisTemplate.opsForValue().set(whitelistenablekey, String.valueOf(whiteList.enable));
//
//            // 白名单配置先删除再保存.
//            redisTemplate.delete(redis.whitelistkey);
//
//            // 如果未开启白名单  或者  白名单为空,则跳过.
//            if (!whiteList.enable || CollUtil.isEmpty(whiteList.ids)) {
//                log.warn("白名单未启用或白名单为空.白名单启用状态:{}", whiteList.enable);
//                return;
//            }
//
//            // 白名单保存到redis
//            Map<String, String> idsToMap = whiteList.ids.stream().collect(Collectors.toMap(e -> e, e -> e, (e, e2) -> e2));
//            redisTemplate.opsForHash().putAll(redis.whitelistkey, idsToMap);
//        } finally {
//            if (redisTemplate != null) {
//                RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
//                if (connectionFactory instanceof LettuceConnectionFactory) {
//                    ((LettuceConnectionFactory) connectionFactory).destroy();
//                }
//            }
//        }
//    }
//
//    private RedisTemplate<String, String> buildRedisTemplate(String hostname, int port, int database, String password) {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(hostname);
//        redisStandaloneConfiguration.setDatabase(database);
//        redisStandaloneConfiguration.setPort(port);
//        if (StrUtil.isNotBlank(password)) {
//            redisStandaloneConfiguration.setPassword(password);
//        }
//        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfigurationBuilder.build());
//        factory.afterPropertiesSet();
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }
//
//
//    @Data
//    static class WhiteListConfig {
//        List<WhiteList> whitelist;
//    }
//
//    @Data
//    static class WhiteList {
//        String group;
//        boolean enable;
//        List<String> ids;
//        RedisConfig redis;
//    }
//
//    @Data
//    static class RedisConfig {
//        String hostname;
//        Integer port;
//        Integer database;
//        String password;
//
//        /**
//         * <pre>
//         * 白名单是否开启
//         * true:开启了白名单,需要再从redis中查询 {@link #whitelistkey} 对应的key来查询白名单.
//         * false:未开启白名单
//         * </pre>
//         */
//        String whitelistenablekey;
//
//        /**
//         * 在redis中,存放白名单配置的key
//         */
//        String whitelistkey;
//    }
//}