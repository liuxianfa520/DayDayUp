package com.anxiaole.easyopen.interceptors.apipermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiuXianfa
 * 
 * @date 2020-12-4 15:47
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseApiPermissionInterceptor {

    String usage = "在api类或方法上,使用此注解:则方法会被登录拦截器:ApiPermissionInterceptor拦截.";


    /**
     * <b>expression断言表达式:使用Groovy脚本编写,脚本需要返回true/false</b>
     *
     * <pre>
     * 作用:
     * 以csc项目举例:
     *      [导出客户]和[导出待分配客户]这两个功能都是调用的同一个easyOpen接口 ： csc.customer.exportCustomer 。只是传参isAssign字段值不同而已。
     *      对应这种同一接口对应两个功能权限的需求,可使用此字段设置一个断言(返回true/false),如果断言为true,则判断是否有 UseApiPermissionInterceptor.apiName 配置的apiName
     * 代码配置:
     *     [导出客户]功能在调用 csc.customer.exportCustomer 接口时,isAssign为空(因为是导出所有的客户,所以不需要使用此参数筛选)
     *     [导出待分配客户]功能在调用 csc.customer.exportCustomer 接口时,isAssign为0   所以:
     *
     * {@code    @UseApiPermissionInterceptor(expression = "customerDto.isAssign == 0", apiName = "csc.customer.exportAssignCustomer")
     * 	   @Api(name = "csc.customer.exportCustomer", wrapResult = false)
     * 	   @ApiDocMethod(description = "导出客户", paramClass = CustomerDto.class)
     * 	   public DataRsp<String> exportCustomer(CustomerDto customerDto) {
     * 	       // 这里省略方法实现
     * 	   }
     * }
     *
     *
     *
     * expression 表达式可使用的变量有:
     *     argu         方法的参数  (如上面例子中,源码中写的方法参数是:CustomerDto customerDto 所以也可以使用 customerDto 来表示参数名.)
     *     request      {@link javax.servlet.http.HttpServletRequest}  request
     *     response     {@link javax.servlet.http.HttpServletResponse}  response
     *     serviceObj   目标方法所在的类   Object 类型
     *     method       目标方法     {@link java.lang.reflect.Method} 类型
     *     opUrl        http请求的url    如果https://service-wbs321.newtamp.cn/csc/api 则opUrl是:/csc/api
     * </pre>
     */
    String expression() default "";

    /**
     * 如果 expression 表达式断言(返回true)则,判断用户是否有此字段配置的接口权限.
     */
    String apiName() default "";

}
