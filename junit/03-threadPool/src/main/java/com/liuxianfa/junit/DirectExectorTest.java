package com.liuxianfa.junit;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;

import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * 1、返回一个Executor实例，具体是DirectExecutor类型，
 * <p>
 * DirectExecutor是一个实现了Executor接口的枚举类。调用execute(Runnable command)方法时，
 * <p>
 * 在当前线程执行任务，而不会另起一个线程。
 * <p>
 * 2、当前线程执行完毕后,会退出.
 *
 * @author LiuXianfa
 * @date 2022/1/27 20:27
 */
public class DirectExectorTest {

    public static void main(String[] args) {
        // 单例对象
        Executor executor = MoreExecutors.directExecutor();
        System.out.println("提交5秒的任务.");
        executor.execute(() -> {
            sleep(5);
            System.out.println(String.format("5秒的任务结束.任务是在[%s]线程中执行的.", Thread.currentThread().getName()));
        });
        System.out.println("main线程结束..");
    }
}
