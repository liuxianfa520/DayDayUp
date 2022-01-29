package com.liuxianfa.junit;

import com.google.common.util.concurrent.MoreExecutors;

import java.time.LocalTime;
import java.util.concurrent.Executor;

import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * 把一个Executor实例包装成一个顺序执行的Executor实例，具体是SequentialExecutor类型。
 * <p>
 * 线程池中的任务,按照任务添加顺序执行.
 * <p>
 * 线程池按照任务添加顺序执行任务。上一个任务执行不完，下一个任务就不会开始。
 *
 * @author LiuXianfa
 * @date 2022/1/27 21:20
 */
public class NewSequentialExecutorTest {

    public static void main(String[] args) {
        Executor executor = MoreExecutors.newSequentialExecutor(JdkThreadPool.JDK_THREAD_POOL);
        for (int i = 0; i < 10; i++) {
            int index = i;
            executor.execute(() -> {
                sleep(index);
                System.out.println(Thread.currentThread().getName() + "@" + index + ", now= " + LocalTime.now());
            });
        }
    }
}
