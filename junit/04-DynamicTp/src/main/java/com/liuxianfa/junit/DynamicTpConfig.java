package com.liuxianfa.junit;

import com.dtp.common.em.QueueTypeEnum;
import com.dtp.common.em.RejectedTypeEnum;
import com.dtp.core.DtpExecutor;
import com.dtp.core.support.DtpCreator;
import com.dtp.core.thread.ThreadPoolBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/29 22:53
 */
@Configuration
public class DynamicTpConfig {

    @Bean
    public DtpExecutor demo1Executor() {
        DtpExecutor executor = DtpCreator.createDynamicFast("demo1Executor");

        // DtpExecutor 是 ThreadPoolExecutor 的子类.可以使用jdk的方法设置线程工厂等参数.
        executor.setThreadFactory(new ThreadFactory() {
            AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "demo1Executor-" + index.getAndIncrement());
            }
        });
        return executor;
    }

    @Bean
    public ThreadPoolExecutor demo2Executor() {
        return ThreadPoolBuilder.newBuilder()
                                .threadPoolName("demo2Executor")
                                .corePoolSize(8)
                                .maximumPoolSize(16)
                                .keepAliveTime(50)
                                .allowCoreThreadTimeOut(true)
                                .workQueue(QueueTypeEnum.SYNCHRONOUS_QUEUE.getName(), null, false)
                                .rejectedExecutionHandler(RejectedTypeEnum.CALLER_RUNS_POLICY.getName())
                                .buildDynamic();
    }
}