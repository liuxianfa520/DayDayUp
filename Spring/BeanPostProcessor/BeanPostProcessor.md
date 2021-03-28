# BeanPostProcessor

![image-20210328223615302](images/image-20210328223615302.png)

- 策略模式
  - 理解：所有的bean后置处理器都实现了相同的接口，每个子类存在不同的实现逻辑：可以根据参数在子类的方法中判断如何进行处理：
    - 有的对bean做了修改
      - aop
        - AbstractAutoProxyCreator#getEarlyBeanReference 在早期引用的时候对目标bean创建代理对象。（只有存在循环依赖时，才会使用此方式创建代理对象）
        - AbstractAutoProxyCreator#postProcessAfterInitialization 在bean初始化完毕后，对目标bean创建代理对象。
    - 有的对bean中方法进行调用
      - AutowiredAnnotationBeanPostProcessor 在postProcessPropertyValues方法中，处理@Autowired、@Value、@Inject 注解
      - CommonAnnotationBeanPostProcessor 在postProcessPropertyValues方法中，处理javax.annotation.Resource、javax.xml.ws.WebServiceRef、javax.ejb.EJB 注解。
      - ApplicationContextAwareProcessor 在postProcessBeforeInitialization方法中，调用invokeAwareInterfaces(Object bean)方法，去调用实现了Aware接口相关方法。
      - InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization调用有javax.annotation.PostConstruct注解的方法
  - 使用策略模式代替了if-else
  - BeanPostProcessor使用了策略模式视频讲解：https://m.bilibili.com/video/BV1uE411d7L5?p=13 （子路老师）







# spring文档

- [容器扩展点之——使用BeanPostProcessor自定义bean](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp)

- [使用BeanPostProcessor实现bean声明周期](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle)