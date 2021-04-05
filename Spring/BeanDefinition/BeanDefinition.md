# BeanDefinition

- BeanDefinition

  [**Table 1. The bean definition**](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-definition)

  |         Property         | Explained in…                                                |
  | :----------------------: | :----------------------------------------------------------- |
  |          Class           | [Instantiating Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-class) |
  |           Name           | [Naming Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-beanname) |
  |          Scope           | [Bean Scopes](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes) |
  |  Constructor arguments   | [Dependency Injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-collaborators) |
  |        Properties        | [Dependency Injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-collaborators) |
  |     Autowiring mode      | [Autowiring Collaborators](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-autowire) |
  | Lazy initialization mode | [Lazy-initialized Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lazy-init) |
  |  Initialization method   | [Initialization Callbacks](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle-initializingbean) |
  |    Destruction method    | [Destruction Callbacks](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle-disposablebean) |

- BeanDefinitionReader

  - ```java
    BeanDefinitionRegistry getRegistry();
    
    int loadBeanDefinitions(Resource resource);
    ```
    
  - 接口定义了：从各种资源中，加载 `BeanDefinition`，并注册到 `BeanDefinitionRegistry` 中
  
  - 返回int：表示从此资源加载的 `BeanDefinition` 的数量
  
  - [官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-client)
  
- BeanDefinitionParser

  - ```java
    BeanDefinition parse(Element element, ParserContext parserContext);
    ```

  - 此接口用于处理**顶级自定义的标签** *(指:<beans/>标签的直接子标签)*

- ClassPathBeanDefinitionScanner

   ```java
    	public int scan(String... basePackages) {
    		int beanCountAtScanStart = this.registry.getBeanDefinitionCount();
    
    		// 包扫描。
    		doScan(basePackages);
    
    		// Register annotation config processors, if necessary.
            // 如果需要,则注册 注解配置处理器.相当于开启<context:annotation-config/> 
    		if (this.includeAnnotationConfig) {
    			AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    		}
    
    		return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
    	}
   ```


# 问题：

- 写的beans.xml配置文件是如何解析成bean定义的？
  -  <bean id="helloService" class="test.HelloService" />
  - 在 `BeanDefinitionReader` 中把资源加载成bean定义。
- 在beans.xml配置文件中，spring默认的标签有哪些？
  - bean
  - import
  - alias
  - `DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`中会调用 `delegate.isDefaultNamespace(root)` 方法来判断是否为默认标签。
- spring是如何认识dubbo的标签的？

  - <dubbo:service interface="com.newbanker.pco.service.MessageConsumerService" ref="messageConsumerService" />
  - 实现`BeanDefinitionParser`
  - `com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler`
  - META-INF\spring.handlers 中配置：http\://code.alibabatech.com/schema/dubbo=com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler
  - 官方文档：[创作XML Schema](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#xml-custom)
- *超纲问题：使用@Controller、@Service、@Component注解的类，spring是如何解析的呢？*
  - *org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry*
  - *这是一个 **bean工厂注册中心后置处理器：BeanDefinitionRegistryPostProcessor***
  - *注意：实际上调用的是 ClassPathBeanDefinitionScanner 的`doScan()`方法，而不是`scan()`方法。*
  - ![image-20210328001114068](images/image-20210328001114068.png)



# 源码阅读

https://github.com/liuxianfa520/spring-framework

org.springframework.beans.factory.xml.XmlBeanFactoryTests#testRegisterBeanDefinition
org.springframework.beans.factory.xml.XmlBeanFactoryTests#testLoadBeanDefinitions



# 参考：

### 官方文档:[Table 1. The bean definition](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-definition)

### 中文文档:[多种bean](https://www.php.cn/manual/view/21581.html)

### BeanDefinition#isAbstract 的一个使用场景:

- 如果你有一个（父）bean定义你希望仅仅作为模版使用，而这个定义明确规定了一个类，你必须把abstract参数设置为true，否则应用程序上下文将试图预先初始化它。
- 官方文档: https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-concise-proxy
- 中文文档: https://www.php.cn/manual/view/21806.html

