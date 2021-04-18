# 前言

周瑜老师公开课【[spring核心扩展点底层原理解析](https://www.bilibili.com/video/BV1GZ4y1c7i6?p=46)】：

- [spring中bean的生命周期](https://www.bilibili.com/video/BV1GZ4y1c7i6?p=47)
- [spring中扩展点底层原理](https://www.bilibili.com/video/BV1GZ4y1c7i6?p=49)
- [spring整合MyBatis底层原理](https://www.bilibili.com/video/BV1GZ4y1c7i6?p=50)

其中对后两个课题有所疑问：想知道spring整合MyBatis底层原理，同时这个原理也体现spring扩展点的真实案例。

所以fork了[mybatis-spring源码](https://github.com/liuxianfa520/mybatis-spring)。自己翻了翻源码。

通过看源码解答心中的疑问，在源码中寻找答案，让看源码效率更高。



# 【疑问】为什么向spring容器中注入 SqlSessionFactoryBean 就可以使用@Autowired获得Mapper?

- 其实是不可以的。通过[mybatis-spring官方getting-start文档](https://github.com/liuxianfa520/mybatis-spring/blob/master/src/site/zh/markdown/getting-started.md)，我们看到：如果使用Mapper接口定义持久层，需要两个步骤：

- 1）配置 SqlSessionFactoryBean：

  ```xml
  <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <!-- 依赖数据源dataSource：在这之前也需要配置数据源dataSource，这里忽略dataSource的配置。-- >
    <property name="dataSource" ref="dataSource" /> 
  </bean>
  ```

- 2）配置 MapperFactoryBean：

  ```xml
  <bean id="userMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
    <property name="mapperInterface" value="org.mybatis.spring.sample.mapper.UserMapper" />
    <property name="sqlSessionFactory" ref="sqlSessionFactory" />
  </bean>
  ```

- 此时在Service就可以使用了：

  ```java
  @Service("userService")
  public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
  
    public User getUserById(String id) {
      return userMapper.getUser(id);
    }
  }
  ```

  有同学就说了，这个例子是只配置了一个UserMapper，那我项目中有很多Mapper接口，这样配置岂不是有多少Mapper接口，就需要配置多少个 `org.mybatis.spring.mapper.MapperFactoryBean`？其实是的！不过有包扫描的方式：



# 【疑问】如果项目中有多个Mapper接口，想以`指定包名`的方式，让框架去扫描Mapper接口应该如何配置？是如何实现的？

- 配置：

  ```xml
  <!-- 配置SqlSessionFactory -->
  <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
     <property name="dataSource" ref="dataSourceFoo" />
  </bean>
  
  <!-- 配置Mapper扫描器 -->
  <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
      <!-- 指定Mapper接口所在的包名。如需配置多个，可用英文逗号或分号分开 -->
  	<property name="basePackage" value="com.xxx.dao" />
  	<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
  </bean>
  ```

- Mapper包扫描器配置类 org.mybatis.spring.mapper.MapperScannerConfigurer

  这个类只是包扫描配置类，是为了配置起来比较方便而生的。具体包扫描的逻辑是在`后置处理bean定义注册器`时委托给  `ClassPathMapperScanner`实现的。

- 重写 `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry` 方法*（表示了是在BeanDefinitionRegistry之后进行自定义处理）*：此方法中创建了`ClassPathMapperScanner`去扫描 `basePackage`。

- `ClassPathMapperScanner`是 `org.springframework.context.annotation.ClassPathBeanDefinitionScanner` 的子类。

  ![image-20210418163321325](images/image-20210418163321325.png)

- 在doScan方法包扫描bean：

  1）先使用spring默认的扫描逻辑扫描出Mapper的bean定义。

  2）然后对这些bean定义做特殊处理：

  ![image-20210418155353451](images/image-20210418155353451.png)

- 其实第一步没啥好多的，就利用了spring默认的包扫描逻辑，扫描出这个包中的所有类，然后包装成bean定义。

- 重要的是第二步 狸猫换太子：把真实的Mapper接口改成 org.mybatis.spring.mapper.MapperFactoryBean

  ![image-20210418160118167](images/image-20210418160118167.png)









# 【疑问】为什么指定Mapper.java 并指定Mapper.xml 就可以对数据库进行crud?

- 以前背面试题时，面试宝典告诉我们：是使用**`jdk动态代理`**实现的。那我们就来一探究竟。
- 这个问题应该是 MyBatis 源码中的内容，而不是 mybatis-spring 中的内容。
- todo：详见文档：[使用jdk动态代理实现的Mapper.java]()



# 【面试题】spring和mybatis整合之后为什么会导致mybatis一级缓存失效？？