package com.liuxianfa.junit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;

import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * 网上说, guava 比较好用的是  可监听的执行器 {@link ListeningExecutorService}、可监听的Future {@link ListenableFuture}
 * <p>
 * 但是其实使用java8也能实现.   而且java8的 {@link CompletableFuture} 让开发者更专注于[任务]和[执行任务时是否需要异步].   并不需要关心线程池.
 *
 * @author LiuXianfa
 * @date 2022/1/27 22:02
 */
public class FuturesTest {
    static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(JDK_THREAD_POOL);

    @SneakyThrows
    public static void main(String[] args) {
//        common();
        transform();
    }

    /**
     * 向线程池提交异步任务1 -> 完成之后,返回结果A -> A transorm B B转换完成后,调用callback
     */
    private static void transform() {
        ListenableFuture future = listeningExecutorService.submit(() -> {
            sleep(5);
            return "hello world!";
        });
        // 异步转换 A 转成 B
        ListenableFuture transform = Futures.transformAsync(future, input -> {
            return listeningExecutorService.submit(() -> input.toString().length());
        }, listeningExecutorService);

        Futures.addCallback(transform, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("任务执行结束. 执行返回值是:" + result);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, listeningExecutorService);
        System.out.println("xxxxxxxxxxxxxxxxxx");


        // note:使用java8实现:   文档:  https://blog.csdn.net/winterking3/article/details/116025829
        CompletableFuture<String> helloFuture = CompletableFuture.supplyAsync(() -> "hello world!", JDK_THREAD_POOL);
        CompletableFuture<Integer> lengthFuture = helloFuture.thenApplyAsync(s -> s.length(), JDK_THREAD_POOL);
        // 这相当于成功时的回调
        lengthFuture.thenAccept(result -> System.out.println("任务执行结束. 执行返回值是:" + result));

        // 如果执行抛出异常时,java8如何获取这个异常呢?
        lengthFuture.whenComplete((result, throwable) -> {
            if (throwable != null) { // 异常时,throwable不为null
                throwable.printStackTrace();
            } else {
                System.out.println("任务执行结束. 执行返回值是:" + result);
            }
        });

        // 异常情况. whenComplete方法的参数throwable 就不为null了.
        helloFuture.thenApplyAsync(s -> 1 / 0, JDK_THREAD_POOL)
                   .whenComplete((integer, throwable) -> {
                       throwable.printStackTrace();
                   });
    }

    private static void common() throws InterruptedException, java.util.concurrent.ExecutionException {

        // 使用guava线程池实现:
        ListenableFuture<String> future1 = listeningExecutorService.submit(() -> "Hello");
        ListenableFuture<String> future2 = listeningExecutorService.submit(() -> "World");

        System.out.println(String.join(" ", Futures.allAsList(future1, future2).get()));


        // 使用java8实现:
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello", JDK_THREAD_POOL);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World", JDK_THREAD_POOL);
        String x = String.join(" ", CompletableFuture.allOf(f1, f2).thenApply(unused -> Stream.of(f1, f2).map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList())).get());
        System.out.println(x);
    }
}
