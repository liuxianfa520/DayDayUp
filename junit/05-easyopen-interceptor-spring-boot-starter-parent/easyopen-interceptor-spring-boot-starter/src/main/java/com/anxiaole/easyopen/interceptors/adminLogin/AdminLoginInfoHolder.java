package com.anxiaole.easyopen.interceptors.adminLogin;

import com.anxiaole.BaseLoginInfo;

import com.gitee.easyopen.ApiContext;

/**
 * @author LiuXianfa
 * 
 * @date 12/1 18:47
 */
public class AdminLoginInfoHolder {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();
    private static final ThreadLocal<BaseLoginInfo> loginInfo = new ThreadLocal<>();


    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    static void setLoginInfo(BaseLoginInfo baseLoginInfo) {
        loginInfo.set(baseLoginInfo);
    }

    public static BaseLoginInfo getLoginInfo() {
        return loginInfo.get();
    }

    /**
     * easyopen接口调用时,如果传的access_token为mock,则返回模拟的用户信息
     *
     * @return
     */
    public static boolean isMock() {
        String accessToken = ApiContext.getApiParam().fatchAccessToken();
        return "mock".equals(accessToken);
    }

    public static void clearAll() {
        loginInfo.remove();
        tokenHolder.remove();
    }
}
