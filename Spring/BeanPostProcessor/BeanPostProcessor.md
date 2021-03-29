# bean后置处理器

顶级接口是 org.springframework.beans.factory.config.BeanPostProcessor

子接口结构图如下：

![image-20210328223615302](images/image-20210328223615302.png)



# BeanPostProcessor

此接口是顶级接口。存在的两个方法，可以对每个bean，在初始化前后，做一些自定义的操作。

- Object postProcessBeforeInitialization(Object bean, String beanName);
  - 在bean初始化之前，可以对bean做一些处理。
- Object postProcessAfterInitialization(Object bean, String beanName);
  - 在bean初始化之后，可以对bean做一些处理。

### 调用栈：

![image-20210329162956116](images/image-20210329162956116.png)

通过调用栈，可以看到：

1、在doCreateBean()创建bean过程中，填充bean中的属性之后，会调用 `initializeBean(beanName, exposedObject, mbd);`去初始化bean：

![image-20210329163333211](images/image-20210329163333211.png)

具体初始化流程：

![image-20210329163718566](images/image-20210329163718566.png)

### 应用：

org.springframework.context.support.ApplicationContextAwareProcessor

结构：是 BeanPostProcessor 的直接实现类。![image-20210329165312757](images/image-20210329165312757.png)

作用：

![image-20210329165141944](images/image-20210329165141944.png)





# InstantiationAwareBeanPostProcessor

根据接口名可以理解：这是个可以感知bean实例化的bean后置处理器。

也就是可以在bean实例化之前/之后做一些操作。

![image-20210329170455040](images/image-20210329170455040.png)

- ```java
  Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;
  ```

  - 在spring默认的bean创建(实例化)之前，如果后置处理器返回一个bean实例（bean !=null），则直接拿去使用。
  - ![image-20210329173739466](images/image-20210329173739466.png)
  - ![image-20210329174120865](images/image-20210329174120865.png)
  - 应用：
    - 

- ```java
  boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;
  ```

  - 在bean实例化之后，做一些操作。
  - 返回值boolean：如果需要给当前bean填充属性则返回true；否则返回false。默认为true 。返回false还将防止在此bean上调用任何后续的InstantiationAwareBeanPostProcessor实例。【bean填充的短路操作】
  - ![image-20210329172148248](images/image-20210329172148248.png)
  - 应用：
    - 

- ```java
  PropertyValues postProcessPropertyValues(
       PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException;
  ```

  - ![image-20210329174647745](images/image-20210329174647745.png)
  - 应用：
    - AutowiredAnnotationBeanPostProcessor#postProcessPropertyValues 
    - CommonAnnotationBeanPostProcessor#postProcessPropertyValues











# DestructionAwareBeanPostProcessor

结构：是 BeanPostProcessor 的子接口。

![image-20210329165525585](images/image-20210329165525585.png)

新增了两个方法：

```java
void postProcessBeforeDestruction(Object bean, String beanName)
```

在bean销毁方法调用之前，可以做一些事情。

```java
default boolean requiresDestruction(Object bean) {
   return true;
}
```

确定给定的bean实例是否需要此后处理器销毁。
默认实现返回true 。 如果DestructionAwareBeanPostProcessor的5之前版本的实现未提供此方法的具体实现，则Spring也会默默假定为true 。

















# 设计模式

- 使用了策略模式

- 理解：所有的bean后置处理器都实现了相同的接口，每个子类存在不同的实现逻辑：可以根据参数在子类的方法中判断如何进行处理：
  - 有的对bean做了修改
    - aop
      - AbstractAutoProxyCreator#getEarlyBeanReference 在早期引用的时，对目标bean创建代理对象。（只有存在循环依赖时，才会使用此方式创建代理对象）
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