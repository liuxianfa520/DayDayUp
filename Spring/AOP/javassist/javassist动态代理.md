# 使用javassist作为动态代理
- MyBatis 懒加载默认使用javassist作为动态代理
- 详见: org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory#crateProxy



# Getting Start

一、引入依赖

```xml
        <dependency>
            <groupId>org.javassist</groupId>
            <artifactId>javassist</artifactId>
            <version>3.27.0-GA</version>
        </dependency>
```


二、测试类：

详见：[com.anxiaole.javassist.proxy.JavassistProxyTest](.\src\main\java\com\anxiaole\javassist\proxy\JavassistProxyTest.java)

