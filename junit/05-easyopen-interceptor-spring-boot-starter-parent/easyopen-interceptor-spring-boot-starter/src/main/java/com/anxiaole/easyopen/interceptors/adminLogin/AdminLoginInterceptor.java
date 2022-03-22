package com.anxiaole.easyopen.interceptors.adminLogin;

import com.anxiaole.easyopen.interceptors.login.LoginInfoHolder;
import com.anxiaole.easyopen.utils.ResponseUtils;
import com.anxiaole.framework.httpclient.ApiHttpUtil;
import com.anxiaole.passport.enums.NbError;
import com.anxiaole.passport.exception.NbException;
import com.anxiaole.passport.vo.BaseLoginInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.exception.ApiException;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 12/1 18:45
 */
public class AdminLoginInterceptor extends ApiInterceptorAdapter {

    @Autowired
    CcLoginEasyOpenApiProperties apiProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        try {
            if (LoginInfoHolder.isMock()) {
                logger.info("mock登录信息!");
                return true;
            }

            String token = request.getHeader("token");
            AdminLoginInfoHolder.setToken(token);

            BaseLoginInfo baseLoginInfo = getBaseLoginInfo(token);
            AdminLoginInfoHolder.setLoginInfo(baseLoginInfo);


            if (LoginInfoHolder.getLoginInfo() == null) {
                ResponseUtils.responseError(response, NbError.NO_LOGIN.key, NbError.NO_LOGIN.value);
                return false;
            }

            return true;
        } catch (NbException e) {
            logger.error("vas管理员获取登录信息异常:", e);
            ResponseUtils.responseError(response, e.getMessage());
            return false;
        } catch (Exception e) {
            String msg = "vas管理员获取登录信息未知异常!";
            logger.error(msg, e);
            ResponseUtils.responseError(response, msg);
            return false;
        }
    }

    private BaseLoginInfo getBaseLoginInfo(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("token", token);
        String response = ApiHttpUtil.getResponse(apiProperties.getAppKey(), apiProperties.getSecret(), CcLoginEasyOpenApiProperties.apiName, apiProperties.getApiUrl(), Collections.emptyMap(), headers);
        logger.info("顾问云-获取登录用户信息，response={}", response);

        JSONObject rsp = JSON.parseObject(response);
        if (rsp == null || !"0".equals(rsp.getString("code"))) {
            logger.error("顾问云-获取登录用户信息-错误：{}", JSON.toJSONString(rsp));
            throw new ApiException("顾问云-获取登录用户信息错误:" + rsp.getString("msg"), rsp.getString("code"));
        }
        JSONObject userInfo = rsp.getJSONObject("data");
        if (userInfo == null) {
            throw new ApiException("顾问云-获取登录用户信息不存在!", "400");
        }
        if (Objects.equals(userInfo.get("status"), 0)) { // sys_user表.状态  0：禁用   1：正常
            throw new ApiException("用户已被禁用!可以联系管理员开启!", "400");
        }

        BaseLoginInfo baseLoginInfo = new BaseLoginInfo();
        baseLoginInfo.setCreateTime(new Date(userInfo.getLong("createTime")));
        baseLoginInfo.setCreateTimeLong(userInfo.getLong("createTime"));
        baseLoginInfo.setDepId(userInfo.getInteger("deptId"));
        baseLoginInfo.setMobile(userInfo.getString("mobile"));
        baseLoginInfo.setId(userInfo.getInteger("userId"));
        baseLoginInfo.setName(userInfo.getString("username"));
        return baseLoginInfo;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        LoginInfoHolder.clearAll();
    }

    @Override
    public boolean match(ApiMeta api) {
        return api.getMethod().getAnnotation(IgnoreAdminLoginInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseAdminLoginInterceptor.class) != null || api.getMethod().getAnnotation(UseAdminLoginInterceptor.class) != null;
    }

}
