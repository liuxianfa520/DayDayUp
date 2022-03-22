package com.anxiaole.easyopen.utils;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 7/15 0015 15:55
 */
public class ResponseUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

    public static void responseError(HttpServletResponse response, String msg) {
        responseError(response, 500, msg);
    }

    public static void responseError(HttpServletResponse response, int code, String msg) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", code);
            map.put("msg", msg);
            writer.write(JSON.toJSONString(map));
            writer.flush();
        } catch (IOException e) {
            logger.error("responseError异常!", e);
        }
    }
}
