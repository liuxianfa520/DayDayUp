package com.liuxianfa.junit.springboot.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cn.hutool.http.HttpUtil;

/**
 * Springboot启动后执行方法（4种）
 * <p>
 * https://www.cnblogs.com/lizm166/p/16542073.html
 */
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    @Value("${server.port:8080}")
    String port;

    @Override
    public void run(String... args) throws Exception {
//        // 项目启动之后,先调用此接口
//        String s = HttpUtil.get(String.format("http://localhost:%s/hello?name=xxx", port));
//        System.out.println(String.format("Springboot启动后执行方法,返回值:[%s]", s));
    }
}