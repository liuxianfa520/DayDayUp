package com.pandora.storage.es.exception.config;


import com.pandora.storage.es.exception.MeheExceptionHandler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/5/13 14:27
 */
@Configuration
public class ExceptionHandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    public MeheExceptionHandler storageExceptionHandler() {
        return new MeheExceptionHandler();
    }
}
