package com.liuxianfa.junit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * <pre>
 * 疑问: ForkJoinPool 线程有多少个线程?
 *
 * ForkJoinPool 本质是一个线程池，默认的线程数量是cpu的核数减1:
 *
 * parallelism = Runtime.getRuntime().availableProcessors() - 1
 *
 * 详见: java.util.concurrent.ForkJoinPool#makeCommonPool()
 *
 * </pre>
 *
 * @author xianfaliu2@creditease.cn
 * @date 2022/2/11 18:31
 */
public class ForkJoinPoolTest {

    static ForkJoinPool commonForkJoinPool = ForkJoinPool.commonPool();

    static ForkJoinPool forkJoinPool = new ForkJoinPool(3,// 并行性  线程数量
                                                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,  // 线程工厂
                                                        null,
                                                        true);


    public static void main(String[] args) throws InterruptedException {
        // 打印行数,就是cpu数量减1
        IntStream.rangeClosed(1, 100)
                 .forEach(finalI -> CompletableFuture.runAsync(() -> {
                     System.out.println(finalI);
                     sleep(50);
                 }, commonForkJoinPool));

        sleep(5);
    }
}