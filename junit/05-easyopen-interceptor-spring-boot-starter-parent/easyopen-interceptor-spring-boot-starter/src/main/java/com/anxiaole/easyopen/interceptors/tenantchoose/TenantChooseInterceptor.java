package com.anxiaole.easyopen.interceptors.tenantchoose;

import com.anxiaole.easyopen.interceptors.apipermission.IgnoreApiPermissionInterceptor;
import com.anxiaole.easyopen.interceptors.apipermission.UseApiPermissionInterceptor;
import com.anxiaole.easyopen.interceptors.login.IgnoreLoginInterceptor;
import com.anxiaole.easyopen.interceptors.login.UseLoginInterceptor;
import com.anxiaole.framework.redis.RedisPrefixHolder;
import com.anxiaole.udsc.config.NBEntHolder;
import com.anxiaole.udsc.config.ds.NBDataSourceContextHolder;
import com.anxiaole.udsc.config.redis.NBRedisContextHolder;

import com.anxiaole.easyopen.utils.ResponseUtils;
import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;
import com.xxl.conf.core.XxlConfClient;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.anxiaole.easyopen.utils.ResponseUtils.responseError;


/**
 * 租户切换拦截器(根据域名获取企业信息拦截器)
 *
 * @author LiuXianfa
 * 
 * @date 2020-12-01
 */
public class TenantChooseInterceptor extends ApiInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        int entId = 0;
        String serverName = null;
        try {
            String entIdStr = request.getHeader("entId");
            if (StringUtils.isNotBlank(entIdStr)) {
                entId = Integer.parseInt(entIdStr);
            } else {
                // 获取并根据规则改写请求域名
                String sname = request.getServerName();
                int indexOfdot = sname.indexOf(".");
                String subdomain = sname.substring(0, indexOfdot);
                int indexOf = subdomain.indexOf("-");
                String zkkey = null;
                if (indexOf > 0) {
                    zkkey = sname.substring(indexOf + 1);
                } else {
                    zkkey = sname.substring(indexOfdot + 1);
                }
                serverName = zkkey.replace('.', '_');
                entId = XxlConfClient.getInt(serverName);
            }

            request.setAttribute("entId", entId);

            NBEntHolder.setEntId(entId);
            RedisPrefixHolder.setPrefix(entId);
            NBRedisContextHolder.setTenant("ent" + entId);
            NBDataSourceContextHolder.setTenant("ent" + entId);

        } catch (Exception e) {
            String msg = String.format("企业上下文信息获取异常!域名[%s]", (serverName == null ? request.getServerName() : serverName));
            logger.error(msg, e);
            ResponseUtils.responseError(response, msg);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu, Object result, Exception e) throws Exception {
        // 清理租户信息
        RedisPrefixHolder.setPrefix(null);
        NBRedisContextHolder.setTenant(null);
        NBDataSourceContextHolder.setTenant(null);
        NBEntHolder.setEntId(null);
    }

    @Override
    public boolean match(ApiMeta api) {
        boolean enableTenantChooseInterceptor = api.getMethod().getAnnotation(IgnoreTenantChooseInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseTenantChooseInterceptor.class) != null || api.getMethod().getAnnotation(UseTenantChooseInterceptor.class) != null;

        boolean enableApiPermissionInterceptor = api.getMethod().getAnnotation(IgnoreApiPermissionInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseApiPermissionInterceptor.class) != null || api.getMethod().getAnnotation(UseApiPermissionInterceptor.class) != null;

        boolean enableLoginInterceptor = api.getMethod().getAnnotation(IgnoreLoginInterceptor.class) == null &&
                api.getHandler().getClass().getAnnotation(UseLoginInterceptor.class) != null || api.getMethod().getAnnotation(UseLoginInterceptor.class) != null;
        // 由于 ApiPermissionInterceptor 和 LoginInterceptor 依赖于TenantChooseInterceptor,so:如果方法上启用了 ApiPermissionInterceptor 或者 LoginInterceptor 拦截器,同时也会启用TenantChooseInterceptor
        return enableTenantChooseInterceptor || enableApiPermissionInterceptor || enableLoginInterceptor;
    }
}
