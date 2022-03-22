package com.anxiaole.easyopen.interceptors.login;

import com.anxiaole.easyopen.utils.ResponseUtils;
import com.anxiaole.passport.enums.NbError;

import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 12/1 18:45
 */
public class LoginInterceptor extends ApiInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        if (LoginInfoHolder.isMock()) {
            logger.info("mock登录信息!");
            return true;
        }

        String token = request.getHeader("token");
        LoginInfoHolder.setLoginInfo(token);

        if (LoginInfoHolder.getLoginInfo() == null) {
            ResponseUtils.responseError(response, NbError.NO_LOGIN.key, NbError.NO_LOGIN.value);
            return false;
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        LoginInfoHolder.clearAll();
    }

    @Override
    public boolean match(ApiMeta api) {
        return api.getMethod().getAnnotation(IgnoreLoginInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseLoginInterceptor.class) != null || api.getMethod().getAnnotation(UseLoginInterceptor.class) != null;
    }
}
