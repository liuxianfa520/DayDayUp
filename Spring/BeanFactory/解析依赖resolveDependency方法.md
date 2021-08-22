解析依赖的方法,是在`AutowireCapableBeanFactory`中的`resolveDependency()`方法：

```java
public interface AutowireCapableBeanFactory extends BeanFactory {
    Object resolveDependency(DependencyDescriptor descriptor, 
                             String requestingBeanName,
                             Set<String> autowiredBeanNames, 
                             TypeConverter typeConverter);
}    
```

这个方法在自动注入的时候，用到了。详见：[@Autowired注解源码实现](../@Autowired注解源码实现/readme.md#AutowiredFieldElement)

