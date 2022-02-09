# 背景描述

在同一个SpringBoot项目中，需要连接两个数据库。所以需要配置多数据源。

多数据源配置方式详见： https://gitee.com/anxiaole/DayDayUp/tree/master/junit/02-dynamicDatasources



这种方式肯定是没有问题的，因为在公司的之前一个项目已经实现了。

但是在修改另外一个项目时：需要把druid连接池改成 HikariDataSource 修改完毕后，启动时，就**循环依赖**了。

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

   channelFilter
      ↓
   channelService
      ↓
   customerChannelMapper defined in file [D:\WorkSpace\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]
      ↓
   sqlSessionFactory defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]
┌─────┐
|  dataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  firstDataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker
```



# 解决方法

在网上找到下面这篇文章，顺利解决问题。
r
**[使用springboot 整合多数据源 遇到的坑](https://blog.csdn.net/K_Tang/article/details/83117354)**

产生问题的代码如下:

```java
@Configuration
public class DynamicDataSourceConfig {

    @Bean("firstDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.first")
    public DataSource firstDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.second")
    public DataSource secondDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("dataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource firstDataSource, DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.TYPE_PFMSC, firstDataSource);
        targetDataSources.put(DataSourceType.TYPE_PFMSC_TEST, secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

}

```

根据图中的引用圈可以看 出
1.创建sqlSessionFactory时需要dataSource
2.创建dataSource的时候,需要创建firsrDataSource和secondDataSource
3.然后创建firstDataSource时需要DataSourceInitializerInvoker
4.DataSourceInitializerInvoker又会去引用dataSource造成循环引用

```java
@Configuration
public class DynamicDataSourceConfig {

    @Bean("firstDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.first")
    public DataSource firstDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.second")
    public DataSource secondDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("dataSource")
    @DependsOn({ "firstDataSource", "secondDataSource"})
    @Primary
    public DynamicDataSource dataSource(DataSource firstDataSource, DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.TYPE_PFMSC, firstDataSource);
        targetDataSources.put(DataSourceType.TYPE_PFMSC_TEST, secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

}

```

加入注解 `@DependsOn({ "firstDataSource", "secondDataSource"})` 后可以解决上述问题.
该注解用于声明当前bean依赖于另外一个bean。所依赖的bean会被容器确保在当前bean实例化之前被实例化.



以上都是  **[使用springboot 整合多数据源 遇到的坑](https://blog.csdn.net/K_Tang/article/details/83117354)** 文章中的内容。

主要来分析一下这样修改的原因。



**发生问题的SpringBoot版本号**

spring-boot-autoconfigure-2.2.13.RELEASE

**没有发生循环依赖的SpringBoot版本号：**

spring-boot-autoconfigure-2.6.0



对比了一下这两个版本的代码，发现，在 2.6.0 版本中，已经删除了 `DataSourceInitializerInvoker` 这个类：

```java
/**
 * Bean to handle {@link DataSource} initialization by running {@literal schema-*.sql} on
 * {@link InitializingBean#afterPropertiesSet()} and {@literal data-*.sql} SQL scripts on
 * a {@link DataSourceSchemaCreatedEvent}.
 *
 * @author Stephane Nicoll
 * @see DataSourceAutoConfiguration
 */
class DataSourceInitializerInvoker implements ApplicationListener<DataSourceSchemaCreatedEvent>, InitializingBean {
```

从类继承的接口和类上的注释，我们知道这个类是个 `InitializingBean` ，并且主要目的是在 `InitializingBean#afterPropertiesSet()` 方法中，

去执行 `schema-*.sql` 和 `data-*.sql`  脚本文件。



但是在 在 2.6.0 版本中，已经删除了 `DataSourceInitializerInvoker` 这个类。所以在另外一个项目中并没有出现循环依赖。





# 原因分析

## 循环依赖发生原因

我们先来看一下异常日志：

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

   channelFilter
      ↓
   channelService
      ↓
   customerChannelMapper defined in file [D:\WorkSpace\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]
      ↓
   sqlSessionFactory defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]
┌─────┐
|  dataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  firstDataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker
```

也就是：

![image-20220207171545426](images/image-20220207171545426.png)



那么我们看一下代码执行的流程：

显示自动配置数据源：

![image-20220207171822933](images/image-20220207171822933.png)

![image-20220207172006989](images/image-20220207172006989.png)

![image-20220207172357246](images/image-20220207172357246.png)

![image-20220207174916916](images/image-20220207174916916.png)

循环依赖的原因如上4图。

不过这个 `DataSourceInitializerInvoker`要实现的功能是：

> 当容器中`DataSource`数据源`初始化`完毕之后，
>
> 会在 `DataSourceInitializerInvoker#afterPropertiesSet` 方法中，去执行 `schema-*.sql` 和 `data-*.sql`  脚本文件。
>
> 相当于当SpringBoot项目启动完毕后，就去执行数据库的初始化脚本。

其实spring提供这个功能，也不能说他有问题。只是和多数据源结合的时候，就会发生循环依赖问题。

注意：新版本的SpringBoot已经移除了`DataSourceInitializerInvoker`类，所以使用新的SpringBoot版本号也是一种解决办法。

​           可以把SpringBoot版本改成 `2.6.0` 或者更高，亲测可用。







那为什么加上  `@DependsOn({ "firstDataSource", "secondDataSource"})` 注解之后，就不出现循环依赖了呢？

## @DependsOn为何能解决循环依赖

这就要分析Spring创建bean的过程了。[源码](https://gitee.com/anxiaole/spring-framework/blob/5.0.x/spring-beans/src/main/java/org/springframework/beans/factory/support/AbstractBeanFactory.java#L285)



### **使用 `@DependsOn` 注解**

**就在创建`datasource`之前，先去创建`firstDataSource`和`secondDataSource`**

```
channelFilter
channelService
customerChannelMapper
sqlSessionFactory
dataSource // 并没有createBean，只是 markBeanAsCreated(beanName);   记录bean正在创建中
      dependsOn:fundDataSource      // 去创建依赖的fundDataSource
	  new FundDataSource()
		DataSourceInitializerPostProcessor#postProcessAfterInitialization // bean后置处理器
		 getBean(DataSourceInitializerInvoker.class) // 因为创建的bean是DataSource类型,所以获取 DataSourceInitializerInvoker bean
		  DataSourceInitializerInvoker#afterPropertiesSet  //DataSourceInitializerInvoker初始化完之后,会调用afterPropertiesSet方法.
 		   DataSourceInitializerInvoker#getDataSourceInitializer
		    DataSource ds = this.dataSource.getIfUnique();
		     getBean("dataSource")
		         dependsOn:fundDataSource
                  dependsOn:windDataSource
                  new DynamicDataSource(fundDataSource, targetDataSources) // 创建完依赖的两个bean:fundDataSource和windDataSource
             dataSourceInitializer = new DataSourceInitializer(ds, this.properties, this.applicationContext);
             执行 schema-*.sql  和  data-*.sql 脚本文件.
```

```
依赖图:
channelFilter
channelService
customerChannelMapper
sqlSessionFactory
dataSource
    dependsOn:fundDataSource
        DataSourceInitializerInvoker#afterPropertiesSet()
            dataSource
                dependsOn:fundDataSource
                dependsOn:windDataSource
                new DynamicDataSource(fundDataSource, targetDataSources)
```





### 不使用`@DependsOn`注解

不使用`@DependsOn`注解时，创建bean的依赖关系图：

```
***************************
APPLICATION FAILED TO START
***************************

Description:

The dependencies of some of the beans in the application context form a cycle:

   channelFilter
      ↓
   channelService
      ↓
   customerChannelMapper defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]
      ↓
   sqlSessionFactory defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]
┌─────┐
|  dataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  fundDataSource defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]
↑     ↓
|  org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker
└─────┘

```



异常栈：

```java

Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
org.springframework.context.ApplicationContextException: Unable to start web server; nested exception is java.lang.RuntimeException: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelFilter': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelService': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customerChannelMapper' defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:156)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:545)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:747)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:405)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315)
	at org.springframework.boot.builder.SpringApplicationBuilder.run(SpringApplicationBuilder.java:140)
	at org.springblade.core.launch.BladeApplication.run(BladeApplication.java:50)
	at com.mam.Application.main(Application.java:19)
Caused by: java.lang.RuntimeException: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelFilter': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelService': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customerChannelMapper' defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at io.undertow.servlet.core.DeploymentManagerImpl.deploy(DeploymentManagerImpl.java:254)
	at org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory.createDeploymentManager(UndertowServletWebServerFactory.java:293)
	at org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory.getWebServer(UndertowServletWebServerFactory.java:218)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:180)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:153)
	... 8 more
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelFilter': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelService': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customerChannelMapper' defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessProperties(CommonAnnotationBeanPostProcessor.java:321)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1420)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:593)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:207)
	at org.springframework.boot.web.servlet.ServletContextInitializerBeans.getOrderedBeansOfType(ServletContextInitializerBeans.java:211)
	at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAsRegistrationBean(ServletContextInitializerBeans.java:174)
	at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAsRegistrationBean(ServletContextInitializerBeans.java:169)
	at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAdaptableBeans(ServletContextInitializerBeans.java:154)
	at org.springframework.boot.web.servlet.ServletContextInitializerBeans.<init>(ServletContextInitializerBeans.java:86)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.getServletContextInitializerBeans(ServletWebServerApplicationContext.java:253)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.selfInitialize(ServletWebServerApplicationContext.java:227)
	at org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory$Initializer.onStartup(UndertowServletWebServerFactory.java:620)
	at io.undertow.servlet.core.DeploymentManagerImpl$1.call(DeploymentManagerImpl.java:204)
	at io.undertow.servlet.core.DeploymentManagerImpl$1.call(DeploymentManagerImpl.java:186)
	at io.undertow.servlet.core.ServletRequestContextThreadSetupAction$1.call(ServletRequestContextThreadSetupAction.java:42)
	at io.undertow.servlet.core.ContextClassLoaderSetupAction$1.call(ContextClassLoaderSetupAction.java:43)
	at io.undertow.servlet.core.DeploymentManagerImpl.deploy(DeploymentManagerImpl.java:252)
	... 12 more
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'channelService': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customerChannelMapper' defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessProperties(CommonAnnotationBeanPostProcessor.java:321)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1420)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:593)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:207)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.resolveBeanByName(AbstractAutowireCapableBeanFactory.java:453)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.autowireResource(CommonAnnotationBeanPostProcessor.java:527)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.getResource(CommonAnnotationBeanPostProcessor.java:497)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor$ResourceElement.getResourceToInject(CommonAnnotationBeanPostProcessor.java:650)
	at org.springframework.beans.factory.annotation.InjectionMetadata$InjectedElement.inject(InjectionMetadata.java:228)
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:119)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessProperties(CommonAnnotationBeanPostProcessor.java:318)
	... 32 more
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customerChannelMapper' defined in file [D:\WorkSpace\YiXin\mammoth-biz\blade-service\mam-service\target\classes\com\mam\account\dao\mapper\CustomerChannelMapper.class]: Unsatisfied dependency expressed through bean property 'sqlSessionFactory'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireByType(AbstractAutowireCapableBeanFactory.java:1524)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1404)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:593)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:207)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.resolveBeanByName(AbstractAutowireCapableBeanFactory.java:453)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.autowireResource(CommonAnnotationBeanPostProcessor.java:527)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.getResource(CommonAnnotationBeanPostProcessor.java:497)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor$ResourceElement.getResourceToInject(CommonAnnotationBeanPostProcessor.java:650)
	at org.springframework.beans.factory.annotation.InjectionMetadata$InjectedElement.inject(InjectionMetadata.java:228)
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:119)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessProperties(CommonAnnotationBeanPostProcessor.java:318)
	... 46 more
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'sqlSessionFactory' defined in class path resource [com/baomidou/mybatisplus/autoconfigure/MybatisPlusAutoConfiguration.class]: Unsatisfied dependency expressed through method 'sqlSessionFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:799)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:540)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1336)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1176)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:556)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1307)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1227)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireByType(AbstractAutowireCapableBeanFactory.java:1509)
	... 60 more
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Unsatisfied dependency expressed through method 'dataSource' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:799)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:540)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1336)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1176)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:556)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1307)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1227)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:886)
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:790)
	... 73 more
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'fundDataSource' defined in class path resource [com/mam/common/datasources/DynamicDataSourceConfig.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:602)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1307)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1227)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:886)
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:790)
	... 87 more
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker': Invocation of init method failed; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1794)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:594)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:516)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:324)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:227)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveNamedBean(DefaultListableBeanFactory.java:1175)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveBean(DefaultListableBeanFactory.java:420)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:349)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:342)
	at org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerPostProcessor.postProcessAfterInitialization(DataSourceInitializerPostProcessor.java:52)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(AbstractAutowireCapableBeanFactory.java:430)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1798)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:594)
	... 97 more
Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'dataSource': Requested bean is currently in creation: Is there an unresolvable circular reference?
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.beforeSingletonCreation(DefaultSingletonBeanRegistry.java:355)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:227)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:322)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1307)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory$DependencyObjectProvider.getIfUnique(DefaultListableBeanFactory.java:1969)
	at org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker.getDataSourceInitializer(DataSourceInitializerInvoker.java:98)
	at org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvoker.afterPropertiesSet(DataSourceInitializerInvoker.java:61)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1853)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1790)
	... 111 more

```



是因为在创建 `dataSource` 这个bean时，在创建单例bean时，会先记录并判断是否存在循环依赖：*(简化后的伪代码)*

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory)
    // 【这里校验是否存在循环依赖】创建单例bean之前,校验是否存在循环依赖.
    beforeSingletonCreation(beanName);

    // 创建bean
	singletonObject = singletonFactory.getObject();
	boolean newSingleton = true;
	// 如果这个单例bean是新创建的,则添加到单例
	if (newSingleton) {
	    // 如果创建完单例bean，则添加到一级缓存(singletonObjects)，并移除其他级缓存.
		addSingleton(beanName, singletonObject); // spring bean
	}
	return singletonObject;
}

// 在单例bean创建之前,先校验是否存在循环依赖.
// singletonsCurrentlyInCreation 正在创建中的单例bean,第一次可以添加成功;如果存在循环依赖的话,第二次add就失败,就说明存在循环依赖.
protected void beforeSingletonCreation(String beanName) {
	if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
		throw new BeanCurrentlyInCreationException(beanName);
    }
}
```









需要注意的是：

```
使用 @DependsOn 注解标注的循环依赖,Spring无法自动解决.
```



































