# spring循环依赖
[spring官方文档：依赖处理过程](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-dependency-resolution)



# 问题

- spring能解决哪种循环依赖问题？
  - 单例bean setter方式依赖注入的循环依赖问题。
  - 通过构造器注入的循环依赖问题，无法解决。
  - prototype bean的循环依赖问题，无法解决。
  - 会抛出异常： `BeanCurrentlyInCreationException`
- 是通过哪种方式解决的？
- spring ioc容器中的三级缓存是什么？能否改成二级缓存？




# 测试用例

https://github.com/liuxianfa520/spring-framework

com.atguigu.test.circularReference.useSetter.CircularDependencyTest




![Spring解决循环依赖的步骤【简图】](Spring解决循环依赖的步骤【简图】.jpg)
![Spring解决循环依赖的步骤](Spring解决循环依赖的步骤.png)





# 无法解决的循环依赖

## 原型模式

com.atguigu.test.circularReference.prototype.CircularDependencyWithPrototypeBeanTest

![image-20210320164951687](images/image-20210320164951687.png)

## 构造器方式

com.atguigu.test.circularReference.useConstructor.CircularDependencyUseConstructorTest

![image-20210320165141492](images/image-20210320165141492.png)

## 最终bean被包装 has eventually been wrapped

com.atguigu.test.circularReference.withEventuallyWrappedBean.withEventuallyWrappedBean.CircularDependencyWithEventuallyWrappedBeanTest

com.atguigu.test.circularReference.withEventuallyWrappedBean.withAsyncAnnotation.CircularDependencyWithAsyncTest

![image-20210321203549648](images/image-20210321203549648.png)