# 概述
- 工厂模式
- `org.springframework.beans` 包和 `org.springframework.context` 包是 Spring Framework 的 IoC 容器的基础。
- [`BeanFactory`](https:docs.spring.iospring-frameworkdocs5.3.5javadoc-apiorgspringframeworkbeansfactoryBeanFactory.html) 接口提供了一种能够管理任何类型对象的高级配置机制。 [`ApplicationContext`](https:docs.spring.iospring-frameworkdocs5.3.5javadoc-apiorgspringframeworkcontextApplicationContext.html)是`BeanFactory`的一个子接口,`ApplicationContext`增加了如下功能:
  - 更容易与 Spring 的 AOP 特性集成
  - 消息资源处理（用于国际化）
  - 事件发布
  - 应用层特定上下文，例如用于 Web 应用程序的`WebApplicationContext`。
- 简而言之，`BeanFactory` 提供了配置框架和基本功能，而`ApplicationContext` 添加了更多企业特定的功能。
> - *来源：[spring官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction)   原文:*
  - *The `org.springframework.beans` and `org.springframework.context` packages are the basis for Spring Framework’s IoC container.*
  - *The [`BeanFactory`](https://docs.spring.io/spring-framework/docs/5.3.5/javadoc-api/org/springframework/beans/factory/BeanFactory.html) interface provides an advanced configuration mechanism capable of managing any type of object. [`ApplicationContext`](https://docs.spring.io/spring-framework/docs/5.3.5/javadoc-api/org/springframework/context/ApplicationContext.html) is a sub-interface of `BeanFactory`. It adds:*
      - *Easier integration with Spring’s AOP features*
      - *Message resource handling (for use in internationalization)*
      - *Event publication*
      - *Application-layer specific contexts such as the `WebApplicationContext` for use in web applications.*
  - *In short, the `BeanFactory` provides the configuration framework and basic functionality, and the `ApplicationContext` adds more enterprise-specific functionality.*



# BeanFactory

- `BeanFactory`是访问 Spring bean 容器的根接口，是 bean 容器的基本客户端视图；

- 该接口的实现，持有了一些`bean定义`，每个`bean定义`由一个字符串名称唯一标识。根据 `bean 定义`，`bean工厂`将返回包含对象的独立实例（原型设计模式），或单例共享实例（单例设计模式的高级替代方案，其中实例是工厂范围内的单例）。返回哪种类型的实例取决于 bean factory 配置：API 是相同的。从 Spring 2.0 开始，根据具体的应用程序上下文可以使用更多范围（例如，网络环境中的`request`和`session`范围）。

- 请注意，通常最好依靠依赖注入（“推送”配置）通过 setter 或构造函数来配置应用程序对象，而不是使用任何形式的“拉”配置，例如 BeanFactory 查找。Spring 的依赖注入功能是使用这个 BeanFactory 接口及其子接口实现的。

- 此接口中的所有操作还将检查父工厂是否为 `HierarchicalBeanFactory`。如果在此工厂实例中未找到 bean，则会询问父工厂。此工厂实例中的 Bean 应该覆盖任何父工厂中的同名 Bean。

## bean生命周期
详见:[bean生命周期——初始化.md](./bean生命周期——初始化.md)
详见:[bean生命周期——销毁.md](./bean生命周期——销毁.md)




## 重要方法

```java
package org.springframework.beans.factory;
public interface BeanFactory {
    /**
	 * 用于取消引用 FactoryBean 实例并将其与 FactoryBean 创建的 bean 区分开来。
     * 例如，如果名为 myJndiObject 的 bean 是 FactoryBean，
     * 则获取 &myJndiObject 将返回工厂，而不是工厂返回的实例
	 */
	String FACTORY_BEAN_PREFIX = "&";

	Object getBean(String name) throws BeansException;
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	Object getBean(String name, Object... args) throws BeansException;
	<T> T getBean(Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	boolean containsBean(String name);

	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	String[] getAliases(String name);
}
```

以上方法都很好理解。
















# 问题

- spring bean生命周期是？[参考博客](https://www.jianshu.com/p/1dec08d290c1)
  - 实例化 createBeanInstance()
  - 属性赋值 populateBean()
  - 初始化 initializeBean()
  - 销毁 Destruction
- *spring 实例化bean三种方式 [参考官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-class)
  - 使用构造器
  - 使用静态工厂方法
  - 使用一个工厂方法



# 源码阅读

https://github.com/liuxianfa520/spring-framework

org.springframework.beans.factory.xml.XmlBeanFactoryTests#testHelloService





# 资源

BeanFactory#getBean方法源码讲解视频： https://www.bilibili.com/video/BV1oW41167AV?p=49

强烈推荐的一本书：《Spring源码深度解析（第2版）》

![image-20210317194152027](images/image-20210317194152027.png)

