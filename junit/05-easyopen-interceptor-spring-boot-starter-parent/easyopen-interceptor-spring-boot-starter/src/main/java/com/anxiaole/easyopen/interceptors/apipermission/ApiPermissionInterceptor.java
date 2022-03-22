package com.anxiaole.easyopen.interceptors.apipermission;

import com.anxiaole.easyopen.interceptors.login.LoginInfoHolder;
import com.anxiaole.easyopen.utils.ResponseUtils;
import com.anxiaole.easyopen.utils.ScriptEngineUtil;
import com.anxiaole.passport.enums.SystemUserTypeEnum;
import com.anxiaole.passport.util.LoginInfoUtils;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.bean.ApiDefinition;
import com.gitee.easyopen.bean.DefinitionHolder;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

@Log4j
public class ApiPermissionInterceptor extends ApiInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                ResponseUtils.responseError(response, 400, "请求头中token不能为空!");
                return false;
            }

            LoginInfoHolder.setUserType(token);
            if (LoginInfoHolder.getUserType() != SystemUserTypeEnum.SAAS) {
                // 不是saas后台用户请求，直接返回
                return true;
            }

            String opUrl = request.getRequestURI();
            String apiName = getApiName(argu);

            Map<String, String> map = LoginInfoUtils.getPermission(token, 1);
            if (map != null && map.containsKey(opUrl + "-" + apiName)) {
                return true;
            }

            ResponseUtils.responseError(response, 1010, "沒有权限！");
            return false;
        } catch (Exception e) {
            String msg = "API权限拦截发生未知异常！";
            logger.error(msg, e);
            ResponseUtils.responseError(response, 500, msg);
            return false;
        }
    }

    /**
     * 获取需要校验的 apiName
     *
     * @param argu
     * @return
     */
    private String getApiName(Object argu) {
        ApiDefinition apiDefinition = DefinitionHolder.getByParam(ApiContext.getApiParam());
        UseApiPermissionInterceptor annotation = apiDefinition.getMethod().getAnnotation(UseApiPermissionInterceptor.class);
        if (!annotation.expression().isEmpty()) {
            // 使用groovy脚本引擎执行 @UseApiPermissionInterceptor.expression() 表达式
            Bindings bind = new SimpleBindings();
            bind.put("argu", argu);
            bind.put(getParameterName(apiDefinition.getMethod()), argu); // 尝试读取源码中写的参数名
            bind.put("request", ApiContext.getRequest());
            bind.put("response", ApiContext.getResponse());
            bind.put("serviceObj", apiDefinition.getHandler());
            bind.put("method", apiDefinition.getMethod()); // 目标方法
            bind.put("opUrl", ApiContext.getRequest().getRequestURI());
            try {
                Boolean result = (Boolean) ScriptEngineUtil.getScriptEngine().eval(annotation.expression(), bind);
                logger.info("UseApiPermissionInterceptor expression=[{}],result=[{}]", annotation.expression(), result);
                if (result) {
                    return annotation.apiName();
                } else {
                    logger.warn("UseApiPermissionInterceptor expression=[{}],arug={}", annotation.expression(), JSON.toJSONString(argu));
                }
            } catch (ScriptException e) {
                log.error(String.format("执行脚本异常!expression=[{}]", annotation.expression()), e);
            }
        }

        // 未设置expression表达式 或 执行结果为false 或 执行异常:返回目标方法 {@link com.gitee.easyopen.annotation.Api#name} 配置的apiName
        return apiDefinition.getName();
    }

    private String getParameterName(Object method) {
        if (method instanceof Method) {
            Parameter[] parameters = ((Method) method).getParameters();
            // easyopen的参数最多为1(也可能没有参数)
            if (parameters.length == 1) {
                return parameters[0].getName();
            }
        }
        return "arg"; // 如果获取不到,使用此默认值
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        LoginInfoHolder.clearAll();
    }

    @Override
    public boolean match(ApiMeta api) {
        return api.getMethod().getAnnotation(IgnoreApiPermissionInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseApiPermissionInterceptor.class) != null || api.getMethod().getAnnotation(UseApiPermissionInterceptor.class) != null;
    }
}
