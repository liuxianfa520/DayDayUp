package com.liuxianfa.junit;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.log.Log;

import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * CompletableFuture 相关测试
 *
 * @author xianfaliu
 * @date 2022/2/13 18:11
 */
public class CompletableFutureTest {

    public static void main(String[] args) {
//        completableFuture并行度();
        thenAccept();

//        其中一个任务异常();


    }


    /**
     * thenAccept 方法是否按照提交顺序执行的?
     */
    private static void thenAccept() {
        AtomicInteger atomicInteger = new AtomicInteger();

        for (int i = 0; i < 10; i++) {
            // note:结论: 异步执行时,任务在线程池中. 乱序
            CompletableFuture.completedFuture("")
                             .thenAcceptAsync(s -> {
                                 sleep(1);
                                 System.out.println(Thread.currentThread().getName() + "\t\t" + atomicInteger.incrementAndGet());
                             });
        }


        AtomicInteger atomicInteger2 = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            // note:结论: 不使用异步,任务在main方法中,阻塞、按照提交顺序执行的.
            CompletableFuture.completedFuture("")
                             .thenAccept(s -> {
                                 sleep(1);
                                 System.out.println(Thread.currentThread().getName() + "\t\t" + atomicInteger2.incrementAndGet());
                             });
        }
    }

    /**
     * 说明:总共4个任务,其中i=3这个任务会抛出异常(模拟其中一个任务会抛异常.) 此时在join()处会抛出异常.
     */
    // @Transactional
    private static void 其中一个任务异常() {
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            int finalI = i;
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                sleep(1);
                if (finalI == 3) {
                    throw new RuntimeException("异常.执行的线程:"+Thread.currentThread().getName());
                }
                System.out.println(finalI + "    =====     " + Thread.currentThread().getName());
            });
            list.add(voidCompletableFuture);

            voidCompletableFuture.exceptionally(throwable -> {
                // note:注意:exceptionally()方法,还是在'异步线程'中执行的,而不是提交异步任务的主线程中执行的.
                //      通过这种方式,无法达到事务回滚的目的.
                System.out.println("当前线程名称:"+Thread.currentThread().getName() + "  异步任务出现了异常:" + ExceptionUtil.stacktraceToString(throwable));
                throw new RuntimeException("转换之后的Runtime异常.", throwable);
            });
        }
        System.out.println("任务提交完毕.");
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        // note: 任意一个任务抛出异常时,都不会打印下面这句话.
        //    可以通过这中方式,来监控异步任务是否全部成功,如果全部成功,则提交事务;任意任务失败,则回滚事务.
        System.out.println("所有任务执行完毕.");
        sleep(10);
    }


    /**
     * 测试 CompletableFuture 并行度
     * <p>
     * 场景:假设总共有100个任务.任务编号是从 1~100 ,每个任务执行耗时最多5秒. 目的:每次只能执行10个任务. (相当于并行度为10)
     */
    private static void completableFuture并行度() {
        AtomicInteger integer = new AtomicInteger(1);
        // 设置线程池为固定大小:10个线程.
        JDK_THREAD_POOL.setCorePoolSize(10);
        JDK_THREAD_POOL.setMaximumPoolSize(10);
        Random random = new Random();
        for (int i = 1; i <= 100; i++) {
            CompletableFuture.runAsync(() -> {
                System.out.println(String.format("执行任务编号:%s,当前执行线程:%s", integer.getAndIncrement(), Thread.currentThread().getName()));
                sleep(random.nextInt(5));
            }, JDK_THREAD_POOL);
        }
        System.out.println("任务提交完毕");
    }
}
