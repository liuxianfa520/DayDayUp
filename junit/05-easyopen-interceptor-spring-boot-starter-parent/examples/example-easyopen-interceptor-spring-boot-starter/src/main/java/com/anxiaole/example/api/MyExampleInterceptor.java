package com.anxiaole.example.api;

import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 11/20 23:13
 */
public class MyExampleInterceptor extends ApiInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        logger.info("MyExampleInterceptor - preHandle");
        return super.preHandle(request, response, serviceObj, argu);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result) throws Exception {
        logger.info("MyExampleInterceptor - postHandle");
        super.postHandle(request, response, serviceObj, argu, result);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        logger.info("MyExampleInterceptor - afterCompletion");
        super.afterCompletion(request, response, serviceObj, argu, result, e);
    }

    @Override
    public boolean match(ApiMeta apiMeta) {
        logger.info("MyExampleInterceptor - match");
        return super.match(apiMeta);
    }
}
