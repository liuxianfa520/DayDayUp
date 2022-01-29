package com.liuxianfa.junit;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.liuxianfa.junit.JdkThreadPool.DAEMON_JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * @author LiuXianfa
 * @date 2022/1/27 13:24
 */
public class ExitingExecutorServiceTest {

    public static void main(String[] args) {
//        guava守护线程池();

        使用CompletableFuture实现();
//        使用守护线程工厂();

//        默认情况下JDK线程池中的任务都执行完毕之后JVM也不会退出();

        System.out.println("main 线程执行到最后了.");
    }


    private static void 默认情况下JDK线程池中的任务都执行完毕之后JVM也不会退出() {
        JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第1个任务提交完毕.");


        JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第2个任务提交完毕.");
    }


    private static  void 使用CompletableFuture实现(){
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第1个任务提交完毕.");

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第2个任务提交完毕.");


        // 等待所有future执行完毕.
        CompletableFuture.allOf(future1, future2).join();
    }


    /**
     * 并不会等待两个线程执行完毕之后,再退出jvm进程.
     * <p>
     * 而是直接在main方法执行完毕之后,就退出jvm
     */
    private static void 使用守护线程工厂() {
        DAEMON_JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第1个任务提交完毕.");


        DAEMON_JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第2个任务提交完毕.");
    }

    /**
     * 使用guava守护线程池,如果任务都执行完毕之后jvm会退出.
     */
    private static void guava守护线程池() {
        /*
         * getExitingExecutorService()方法的三个重载： 把一个ThreadPoolExecutor实例转成一个应用结束后自动退出的ExecutorService实例。
         * 这个线程池,最多等待15秒,如果15秒之后任务还是没有执行完毕,那么也会退出.
         */
        int terminationTimeout = 15;
        ExecutorService exitingExecutorService = MoreExecutors.getExitingExecutorService(JDK_THREAD_POOL, terminationTimeout, TimeUnit.SECONDS);
        exitingExecutorService.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第1个任务提交完毕.");

        exitingExecutorService.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("第2个任务提交完毕.");
    }


}
