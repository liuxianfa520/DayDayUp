# 一、作用

为了解决easyopen api接口拦截器复制粘贴问题。

基于spring-boot-starter自动配置，注解驱动，让easyopen拦截器一键可得!



# 二、使用方法

项目要求：SpringBoot + easyopen 项目。

## 1、引入依赖

```xml
<dependency>
    <groupId>com.newbanker</groupId>
    <artifactId>easyopen-xss-filter-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```



## 2、application.properties启用

在 `application.properties`配置中间中，使用 `easyopen.api-interceptor.enable=true`启用此starter组件。

并使用 `easyopen.api-interceptor.enable-interceptors`指定需要启用的拦截器。

```properties
easyopen.api-interceptor.enable=true
easyopen.api-interceptor.enable-interceptors=ApiPermissionInterceptor
```

## 3、easyopen接口
```java
@ApiService
public class CustomerApi extends BaseApi {

    @UseApiPermissionInterceptor // 对此方法使用 api权限拦截器
    @UseLoginInterceptor // 对此方法使用 登录拦截器
	@Api(name = "csc.customer.importCustomer", wrapResult = false)
	@ApiDocMethod(description = "导入客户", paramClass = ImportCustomerDto.class, results = {
			@ApiDocField(name = "code", description = "返回状态码", dataType = DataType.INT, example = "0"),
			@ApiDocField(name = "msg", description = "返回信息", dataType = DataType.STRING, example = "操作成功"),
			@ApiDocField(name = "value", description = "返回结果", beanClass = ImportResultVo.class) })
	public DataRsp<ImportResultVo> importCustomer(ImportCustomerDto dto) {
        // 省略业务代码.....
		return rsp;
	}
}
```

## 4、获取当前登录用户的步骤

只适用于service租户域名获取企业id!

```
* 在easyopen接口方法或者类上加上注解: @UseLoginInterceptor
* 在easyopen接口方法中使用 LoginInfoHolder#getToken 获取token
* 在easyopen接口方法中使用 LoginInfoHolder#getLoginInfo 方法获取当前登录用户信息
* 在easyopen接口方法中使用 LoginInfoHolder#getUserType 获取当前登录用户类别SystemUserTypeEnum
* 在easyopen接口方法中使用 LoginInfoHolder#getInvestorInfo 获取当前登录投资人信息
* 在easyopen接口方法中使用 LoginInfoHolder#getEmployeeUserInfo 获取当前saas后台登录员工信息
* 在easyopen接口方法中使用 LoginInfoHolder#getAdvisorInfo 获取当前登录理财师信息
```

## 5、获取当前企业id的步骤

只适用于service租户域名获取企业id!

```
* 在easyopen接口方法或者类上加上注解: @UseTenantChooseInterceptor
* 在方法中使用 com.newbanker.udsc.config.NBEntHolder#getEntId 方法获取企业id.
```

# 三、实现原理

## 1、实现原理

在easyopen启动之后，使用优先级最小的 监听器`EasyOpenInterceptorsApplicationListener`，从容器中获取所有的 `com.gitee.easyopen.interceptor.ApiInterceptor` 类，添加到easyopen的拦截器list中去。

## 2、注意

如果用户自己定义了拦截器`xxxApiInterceptor`，放到了spring容器中，此时也会生效的——即使没有使用 `apiConfig.setInterceptors(new ApiInterceptor[]{xxxApiInterceptor});` 添加到easyopen中。

