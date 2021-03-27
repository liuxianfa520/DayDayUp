# BeanDefinition



# 问题：

- 写的beans.xml配置文件是如何解析成bean定义的？
```
<bean id="helloService" class="org.springframework.tests.sample.beans.HelloService" />
```
- 在beans.xml配置文件中，spring默认的标签有哪些？
  - bean
  - import
  - alias
  - DefaultBeanDefinitionDocumentReader#parseBeanDefinitions
- 如何自定义bean定义解析器？
- <dubbo:service interface="com.newbanker.pco.service.MessageConsumerService" ref="messageConsumerService" />spring是如何认识dubbo自定义标签的？
- spring官方文档:[**Table 1. The bean definition**](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-definition)





# 源码阅读

https://github.com/liuxianfa520/spring-framework

org.springframework.beans.factory.xml.XmlBeanFactoryTests#testRegisterBeanDefinition
org.springframework.beans.factory.xml.XmlBeanFactoryTests#testLoadBeanDefinitions


