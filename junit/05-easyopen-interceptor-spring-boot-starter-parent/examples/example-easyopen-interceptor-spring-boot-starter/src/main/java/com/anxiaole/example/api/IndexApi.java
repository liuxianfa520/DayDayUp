package com.anxiaole.example.api;

import com.anxiaole.easyopen.interceptors.apipermission.UseApiPermissionInterceptor;
import com.anxiaole.easyopen.interceptors.login.IgnoreLoginInterceptor;
import com.anxiaole.easyopen.interceptors.login.LoginInfoHolder;
import com.anxiaole.easyopen.interceptors.login.UseLoginInterceptor;
import com.anxiaole.easyopen.interceptors.tenantchoose.IgnoreTenantChooseInterceptor;
import com.anxiaole.easyopen.interceptors.xss.UseXssInterceptor;

import com.anxiaole.example.entiy.EmpEntity;
import com.anxiaole.example.entiy.UserEntity;

import com.anxiaole.easyopen.interceptors.tenantchoose.UseTenantChooseInterceptor;

import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;

import java.util.HashMap;

@ApiService
@UseTenantChooseInterceptor
@UseXssInterceptor
@UseLoginInterceptor
public class IndexApi {

    /**
     * name 参数传    "><script>alert('XSS');</script>
     *
     * @param map
     * @return
     */
    @Api(name = "testMap.XssFilterAutoConfiguration", wrapResult = false)
    @ApiDocMethod(description = "测试xss拦截器HashMap", order = 1,
                  params = {@ApiDocField(name = "name", description = "名称", dataType = DataType.STRING),
                          @ApiDocField(name = "empEntity", description = "员工实体", beanClass = EmpEntity.class, dataType = DataType.OBJECT)})
    public HashMap<String, Object> test(HashMap map) {
        System.out.println("TestApi 方法中!" + JSON.toJSONString(map, true));
        System.out.println("getEmployeeUserInfo:"+JSON.toJSONString(LoginInfoHolder.getEmployeeUserInfo(), true));
        return map;
    }

    @Api(name = "testBean.XssFilterAutoConfiguration", wrapResult = false)
    @ApiDocMethod(description = "测试xss拦截器UserEntity", order = 2)
    public UserEntity testBean(UserEntity userEntity) {
        System.out.println("TestApi 方法中!" + JSON.toJSONString(userEntity, true));
        System.out.println("getEmployeeUserInfo:"+JSON.toJSONString(LoginInfoHolder.getEmployeeUserInfo(), true));
        return userEntity;
    }


    @Api(name = "IgnoreXssFilter.XssFilterAutoConfiguration", wrapResult = false)
    @ApiDocMethod(description = "测试@IgnoreXssFilter", order = 3)
    public UserEntity ignoreXssFilter(UserEntity userEntity) {
        System.out.println("TestApi 方法中!" + JSON.toJSONString(userEntity, true));
        System.out.println("getEmployeeUserInfo:"+JSON.toJSONString(LoginInfoHolder.getEmployeeUserInfo(), true));
        return userEntity;
    }

    @IgnoreTenantChooseInterceptor
    @IgnoreLoginInterceptor
    @UseApiPermissionInterceptor(expression = "userEntity.name == 'lxf'",apiName = "test.expression.xxxxx")
    @Api(name = "test.expression", wrapResult = false)
    @ApiDocMethod(description = "测试@UseApiPermissionInterceptor注解的expression断言表达式", order = 4)
    public UserEntity testExpression(UserEntity userEntity) {
        System.out.println("TestApi 方法中!" + JSON.toJSONString(userEntity, true));
        return userEntity;
    }

}
