package com.liuxianfa.junit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LiuXianfa
 * @date 2022/1/27 21:20
 */
public class JdkThreadPool {
    private JdkThreadPool() {
    }

    /**
     * 线程工厂,指定线程的名称.
     * <p>
     * %d   可以指定线程池中的线程下标.从0开始.
     */
    static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("My-thread-%d").build();

    /**
     * jdk线程池
     */
    static ThreadPoolExecutor JDK_THREAD_POOL = new ThreadPoolExecutor(10, 100,
                                                                       0L, TimeUnit.MILLISECONDS,
                                                                       new LinkedBlockingQueue<>(1000),
                                                                       threadFactory,
                                                                       // 拒绝策略:调用者执行.
                                                                       new ThreadPoolExecutor.CallerRunsPolicy());

    static ThreadFactory daemonThreadFactory = new ThreadFactoryBuilder().setNameFormat("MyDaemonThread-%d").setDaemon(true).build();

    static ThreadPoolExecutor DAEMON_JDK_THREAD_POOL = new ThreadPoolExecutor(10, 100,
                                                                              0L, TimeUnit.MILLISECONDS,
                                                                              new LinkedBlockingQueue<>(1000),
                                                                              daemonThreadFactory,
                                                                              // 拒绝策略:调用者执行.
                                                                              new ThreadPoolExecutor.CallerRunsPolicy());


    public static void sleep(long l) {
        try {
            Thread.sleep(l * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
