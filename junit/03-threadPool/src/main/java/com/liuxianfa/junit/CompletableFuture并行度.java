package com.liuxianfa.junit;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * 场景:假设总共有100个任务.任务编号是从 1~100 ,每个任务执行耗时最多5秒.
 * 目的:每次只能执行10个任务. (相当于并行度为10)
 *
 * @author xianfaliu
 * @date 2022/2/13 18:11
 */
public class CompletableFuture并行度 {

    static AtomicInteger integer = new AtomicInteger(1);

    static Random random = new Random();


    public static void main(String[] args) {
        // 设置线程池为固定大小:10个线程.
        JDK_THREAD_POOL.setCorePoolSize(10);
        JDK_THREAD_POOL.setMaximumPoolSize(10);

        for (int i = 1; i <= 100; i++) {
            CompletableFuture.runAsync(() -> {
                System.out.println(String.format("执行任务编号:%s,当前执行线程:%s", integer.getAndIncrement(), Thread.currentThread().getName()));
                sleep(random.nextInt(5));
            }, JDK_THREAD_POOL);
        }
        System.out.println("任务提交完毕");
    }
}
