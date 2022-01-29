package com.liuxianfa.junit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import lombok.SneakyThrows;

import static com.liuxianfa.junit.JdkThreadPool.JDK_THREAD_POOL;
import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * <pre>
 *
 * Future接口为异步计算取回结果提供了一个存根(stub)，
 * 然而这样每次调用Future接口的get方法取回计算结果每每是须要面临阻塞的可能性。
 * 这样在最坏的状况下，异步计算和同步计算的消耗是一致的。
 *
 * 所以,Guava库中提供一个很是强大的装饰后的Future接口，
 * 使用观察者模式为在异步计算完成以后,立刻执行addListener指定一个Runnable对象，从而实现“完成当即通知”。
 *
 * 更多详见:  http://ifeve.com/google-guava-listenablefuture/
 *
 * </pre>
 *
 * @author LiuXianfa
 * @date 2022/1/27 20:21
 */
public class ListeningDecoratorTest {

    static ListeningExecutorService pool = MoreExecutors.listeningDecorator(JDK_THREAD_POOL);

    @SneakyThrows
    public static void main(String[] args) {
        testWithListeningDecorator();

//        testWithCompletableFuture();

        System.out.println("main 线程执行到最后了.");
        sleep(5);
    }

    private static void testWithCompletableFuture() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
            return "[hello pool]";
        });


        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 1 / 0);

        BiConsumer<Object, Throwable> callback = (s, t) -> {
            if (t != null) {
                System.out.println("失败" + Thread.currentThread().getName() + "  异步任务异常:");
                t.printStackTrace();
            } else {
                System.out.println("成功" + Thread.currentThread().getName() + "  异步任务执行结果:" + s);
            }
        };
        future.whenCompleteAsync(callback);
        future2.whenCompleteAsync(callback);
    }

    private static void testWithListeningDecorator() {
        ListenableFuture<String> future = pool.submit(() -> {
            sleep(4);
            System.out.println("线程执行完毕." + Thread.currentThread().getName());
            return "[hello pool]";
        });

        ListenableFuture<Integer> future2 = pool.submit(() -> 1 / 0);


        Futures.addCallback(future, callback, pool);
        Futures.addCallback(future2, callback, pool);
    }


    static FutureCallback<Object> callback = new FutureCallback<Object>() {
        @Override
        public void onSuccess(Object result) {
            System.out.println("成功" + Thread.currentThread().getName() + "  异步任务执行结果:" + result);
        }

        @Override
        public void onFailure(Throwable t) {
            System.out.println("失败" + Thread.currentThread().getName() + "  异步任务异常:");
            t.printStackTrace();
        }
    };

}
