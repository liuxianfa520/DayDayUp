package com.liuxianfa.junit.springboot.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

public class TestHello {

    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                String url = "http://127.0.0.1:8080/hello?name=anxiaole";
                HttpRequest get = HttpUtil.createGet(url);
//                get.header("Connection", "close");
//                get.header("Connection", "keep-alive");
                System.out.println(Thread.currentThread().getName() + "    " + get.execute().body());
            }).start();
        }
    }
}
