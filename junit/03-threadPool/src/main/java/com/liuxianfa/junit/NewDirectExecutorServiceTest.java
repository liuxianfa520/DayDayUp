package com.liuxianfa.junit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import static com.liuxianfa.junit.JdkThreadPool.sleep;

/**
 * 返回一个ListeningExecutorService实例，具体是DirectExecutorService类型，
 * <p>
 * DirectExecutorService是MoreExecutors的内部类，继承了AbstractListeningExecutorService。
 * <p>
 * 和DirectExecutor实例类似，调用DirectExecutorService实例的submit()方法时，会在当前线程执行任务，而不会另起一个线程。
 *
 * @author LiuXianfa
 * @date 2022/1/27 20:31
 */
public class NewDirectExecutorServiceTest {
    static ListeningExecutorService pool = MoreExecutors.newDirectExecutorService();

    static FutureCallback<Object> callback = new FutureCallback<Object>() {
        @Override
        public void onSuccess(Object result) {
            System.out.println("线程执行成功." + Thread.currentThread().getName());
        }

        @Override
        public void onFailure(Throwable t) {
            System.out.println("线程执行失败." + Thread.currentThread().getName());
            t.printStackTrace();
        }
    };

    public static void main(String[] args) {

        System.out.println("提交5秒的任务.");
        ListenableFuture<?> future = pool.submit(() -> {
            sleep(5);
            System.out.printf("5秒的任务结束.任务是在[%s]线程中执行的.%n", Thread.currentThread().getName());
        });


        Futures.addCallback(future, callback, pool);
        System.out.println("main线程结束..");

    }
}