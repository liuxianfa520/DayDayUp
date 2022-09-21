package com.liuxianfa.junit;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread limit exceeded replacing blocked worker 异常实验
 * <p>
 * https://blog.csdn.net/u011039332/article/details/113482540
 */
public class Test09ParallelStreamRejectEx {

    // nThread
    static int N_THREAD = 10;
    // executorService
    static ExecutorService NORMAL_EXECUTOR_SERVICE = new ThreadPoolExecutor(N_THREAD, N_THREAD,
                                                                            10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    static ExecutorService FORK_JOIN_EXECUTOR_SERVICE = ForkJoinPool.commonPool();

    // Test09ParallelStreamRejectEx
    public static void main(String[] args) throws Exception {

        // parallelism : 1 -> 1
        // parallelism : 2 -> 8
        // parallelism : 4 -> 16
        // parallelism : 7 -> 32
        // parallelism : 8 -> 32
        // parallelism : 16 -> 64

        // list
        List<Integer> list = new ArrayList<>();
        for (int j = 0; j < 100_0000; j++) {
            list.add(j);
        }

        for (int i = 0; i < N_THREAD; i++) {
            NORMAL_EXECUTOR_SERVICE.execute(() -> {
                int count = list.parallelStream()
                                .map(integer -> {
                                    Future future = CompletableFuture.runAsync(() -> {
                                    });
                                    try {
                                        Object result = future.get(10, TimeUnit.SECONDS);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return integer;
                                })
                                .reduce(Integer::sum)
                                .orElse(0);
                System.out.println(count);
            });

//            FORK_JOIN_EXECUTOR_SERVICE.submit(() -> {
//                int count = list
//                        .parallelStream()
//                        .map(ele -> {
//                            Future future = CompletableFuture.runAsync(() -> { });
//
//                            try {
//                                Object result = future.get(10, TimeUnit.SECONDS);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            return ele;
//                        })
//                        .reduce(Integer::sum)
//                        .orElse(0);
//                System.out.println(count);
//            });
        }

        int x = 0;
        Thread.sleep(1000_000);
    }

}


/*  报错日志:
2022-09-20 10:19:06.438 [ForkJoinPool.commonPool-worker-117] [] [com.mam.product.strategy.Strategy007]
ERROR: 生成007基**据异常.
org.springframework.data.redis.RedisSystemException: Unknown redis exception; nested exception is java.util.concurrent.RejectedExecutionException: Thread limit exceeded replacing blocked worker
	at org.springframework.data.redis.FallbackExceptionTranslationStrategy.getFallback(FallbackExceptionTranslationStrategy.java:53)
	at org.springframework.data.redis.FallbackExceptionTranslationStrategy.translate(FallbackExceptionTranslationStrategy.java:43)
	at org.springframework.data.redis.connection.lettuce.LettuceConnection.convertLettuceAccessException(LettuceConnection.java:270)
	at org.springframework.data.redis.connection.lettuce.LettuceStringCommands.convertLettuceAccessException(LettuceStringCommands.java:799)
	at org.springframework.data.redis.connection.lettuce.LettuceStringCommands.get(LettuceStringCommands.java:68)
	at org.springframework.data.redis.connection.DefaultedRedisConnection.get(DefaultedRedisConnection.java:260)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.lambda$get$1(DefaultRedisCacheWriter.java:110)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.execute(DefaultRedisCacheWriter.java:248)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.get(DefaultRedisCacheWriter.java:110)
	at org.springframework.data.redis.cache.RedisCache.lookup(RedisCache.java:88)
	at org.springframework.cache.support.AbstractValueAdaptingCache.get(AbstractValueAdaptingCache.java:58)
	at org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:73)
	at org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:571)
	at org.springframework.cache.interceptor.CacheAspectSupport.findCachedItem(CacheAspectSupport.java:536)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:402)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:346)
	at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:61)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:691)
	at com.mam.product.service.SingleProductService$$EnhancerBySpringCGLIB$$29261059.detail(<generated>)
	at com.mam.product.strategy.Strategy007.<init>(Strategy007.java:248)
	at com.mam.product.service.SingleProductAdminService.lambda$generateStrategy007File$6(SingleProductAdminService.java:597)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
	at java.util.stream.ReduceOps$ReduceTask.doLeaf(ReduceOps.java:747)
	at java.util.stream.ReduceOps$ReduceTask.doLeaf(ReduceOps.java:721)
	at java.util.stream.AbstractTask.compute(AbstractTask.java:316)
	at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
	at java.util.concurrent.ForkJoinPool$WorkQueue.execLocalTasks(ForkJoinPool.java:1040)
	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1058)
	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Caused by: java.util.concurrent.RejectedExecutionException: Thread limit exceeded replacing blocked worker
	at java.util.concurrent.ForkJoinPool.tryCompensate(ForkJoinPool.java:2011)
	at java.util.concurrent.ForkJoinPool.managedBlock(ForkJoinPool.java:3310)
	at java.util.concurrent.CompletableFuture.timedGet(CompletableFuture.java:1775)
	at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1915)
	at io.lettuce.core.protocol.AsyncCommand.await(AsyncCommand.java:83)
	at io.lettuce.core.LettuceFutures.awaitOrCancel(LettuceFutures.java:112)
	at io.lettuce.core.FutureSyncInvocationHandler.handleInvocation(FutureSyncInvocationHandler.java:69)
	at io.lettuce.core.internal.AbstractInvocationHandler.invoke(AbstractInvocationHandler.java:80)
	at com.sun.proxy.$Proxy574.get(Unknown Source)
	at org.springframework.data.redis.connection.lettuce.LettuceStringCommands.get(LettuceStringCommands.java:66)
	... 31 common frames omitted
 */