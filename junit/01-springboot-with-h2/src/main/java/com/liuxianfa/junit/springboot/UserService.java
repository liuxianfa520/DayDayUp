package com.liuxianfa.junit.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

import lombok.SneakyThrows;

/**
 * @date 7/19 22:43
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    /**
     * <pre>
     * note: 结论：主线程和子线程的在事务上是相互隔离的，子线程的异常不会影响主线程的事务混滚与否 （让若主线程不主动throw出异常，子线程即使抛出了异常也不会影响主线程的）。
     *  异步线程中,和主线程都不是同一个事务.
     *  举例:在异步方法中,根据userId查询,查询不到数据.
     * </pre>
     */
    @SneakyThrows
    @Async
    @Transactional(rollbackFor = Exception.class)
    public Future run(User user) {
        System.out.println("run: 修改用户名: " + Thread.currentThread().getName());

        user.setName(user.getName() + "  update in UserService");
        userMapper.save(user);

        if (user.getAge() == -1) {
            throw new RuntimeException("年龄不合法.");
        }
        return new AsyncResult(user);
    }
}
