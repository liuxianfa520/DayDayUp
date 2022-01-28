package com.liuxianfa.junit;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/1/27 13:24
 */
public class ExitingExecutorServiceTest {

    public static void main(String[] args) {
        guava守护线程池();

//        jdk线程池中的任务都执行完毕之后jvm也不会退出();

        System.out.println("main 线程执行到最后了.");
    }


    private static void jdk线程池中的任务都执行完毕之后jvm也不会退出() {
        JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("1");


        JDK_THREAD_POOL.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("2");
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
        System.out.println("1");

        exitingExecutorService.execute(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
        });
        System.out.println("2");
    }


}
