package com.liuxianfa.junit;

import com.dtp.core.DtpRegistry;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/29 22:57
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationInitRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DtpRegistry.getExecutor("demo1Executor")
                   .execute(() -> log.info(">>>>>>>>>>>>>>>>>>>项目启动了.当前打印日志的线程名称是:" + Thread.currentThread().getName()));

        DtpRegistry.getExecutor("demo2Executor")
                   .execute(() -> log.info(String.format(">>>>>>>>>>>>>>>>>>>项目启动了.当前打印日志的线程名称是:%s   默认的线程名前缀 详见: ThreadPoolProperties#threadNamePrefix ", Thread.currentThread().getName())));

        // 获取在yaml配置文件中配置的线程池.
        DtpRegistry.getExecutor("demo1-executor-use-yaml-config")
                   .execute(() -> log.info(">>>>>>>>>>>>>>>>>>>项目启动了.当前打印日志的线程名称是:" + Thread.currentThread().getName()));
    }
}
