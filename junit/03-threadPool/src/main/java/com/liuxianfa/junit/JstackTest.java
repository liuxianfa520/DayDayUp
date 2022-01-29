package com.liuxianfa.junit;

import java.util.concurrent.ThreadPoolExecutor;

import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * @author LiuXianfa
 * @date 2022/1/29 21:52
 */
public class JstackTest {

    public static void main(String[] args) {
        ThreadPoolExecutor jdkThreadPool = JdkThreadPool.JDK_THREAD_POOL;
        jdkThreadPool.execute(() -> sleep(100));
        jdkThreadPool.execute(() -> sleep(100));
        jdkThreadPool.execute(() -> sleep(100));
        jdkThreadPool.execute(() -> sleep(100));
    }
}
