package com.liuxianfa.junit.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @date 5/8 17:01
 */
@RestController
public class GetResourceAsStreamController {
    @GetMapping("test")
    public HashMap<Object, Object> test() {
        String url = "/file.txt";
        HashMap<Object, Object> map = new HashMap<>();
        // idea直接运行,返回false. 打成jar,返回true
        map.put("a", GetResourceAsStreamController.class.getClassLoader().getResourceAsStream(url) != null);
        // 打jar或在idea直接运行,都返回true
        map.put("b", GetResourceAsStreamController.class.getResourceAsStream(url) != null);
        return map;
    }
}
