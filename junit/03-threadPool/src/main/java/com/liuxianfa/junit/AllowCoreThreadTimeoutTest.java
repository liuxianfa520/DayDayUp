package com.liuxianfa.junit;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 * 疑问: ThreadPoolExecutor core可以设置成0吗?
 *
 *
 * 线程池核心线程数
 *
 * corePoolSize：核心线程数
 * 核心线程会一直存活，及时没有任务需要执行
 * 当线程数小于核心线程数时，即使有线程空闲，线程池也会优先创建新线程处理
 * 设置 allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
 *
 * https://blog.csdn.net/daimengs/article/details/80948946
 *
 * </pre>
 *
 * @author xianfaliu2
 * @date 2022/2/11 18:27
 */
public class AllowCoreThreadTimeoutTest {

    public static void main(String[] args) {
        ThreadPoolExecutor jdkThreadPool = JdkThreadPool.JDK_THREAD_POOL;
        // 设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
        // 适用场景:如果定时任务1天才执行一次.则可以设置成true.
        //          执行完毕之后,线程池中没有线程空转.
        jdkThreadPool.allowCoreThreadTimeOut(true);
    }
}
