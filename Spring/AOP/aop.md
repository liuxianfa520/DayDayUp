# AbstractAutoProxyCreator

![image-20210329001410343](images/image-20210329001410343.png)

- 抽象自动代理创建器





# 问题

- 默认aop创建动态代理对象有哪两种实现？如何对其进行修改？
  - JDK动态代理
  - Cglib动态代理
  - 设置proxyTargetClass=true就用Cglib动态代理
  - 如果proxyTargetClass=false并且目标class是接口，则用JDK动态代理
- 在bean的哪个生命周期时创建的代理对象？
  - AbstractAutoProxyCreator#postProcessAfterInitialization 在bean初始化完毕后，对目标bean创建代理对象。
  - AbstractAutoProxyCreator#getEarlyBeanReference 在早期引用的时，对目标bean创建代理对象。（只有存在循环依赖时，才会使用此方式创建代理对象）











# JDK动态代理

com.atguigu.test.cglib.CglibTest 一个简单的cglib动态代理测试用例

com.atguigu.test.cglibDynamicProxy.MyCglibDynamicProxyTest 模拟Spring中使用cglib创建代理对象的测试用例







# Cglib动态代理

com.atguigu.test.cglibDynamicProxy.MyCglibDynamicProxyTest















# 参考

## [死磕Spring AOP系列5：设计模式在AOP中的使用](https://blog.51cto.com/dba10g/1786250)

