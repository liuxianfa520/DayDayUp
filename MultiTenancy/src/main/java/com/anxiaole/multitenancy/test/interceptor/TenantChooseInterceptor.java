package com.anxiaole.multitenancy.test.interceptor;

import com.anxiaole.multitenancy.utils.TenantIdHolder;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 4/9 23:05
 */
@Component
public class TenantChooseInterceptor implements HandlerInterceptor {

    private String key = "tenantId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getHeader(key);
        if (StringUtils.hasText(tenantId)) {
            TenantIdHolder.setTenantId(tenantId);
            return true;
        }

        tenantId = request.getParameter(key);
        if (StringUtils.hasText(tenantId)) {
            TenantIdHolder.setTenantId(tenantId);
            return true;
        }

        PrintWriter writer = response.getWriter();
        writer.write("请求头或请求参数中需要传 tenantId !");
        writer.flush();
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantIdHolder.clear();
    }
}
