在spring的bean中，我们可以使用以下两个注释，标注初始化方法和销毁方法：

- @PostConstruct
- @PreDestroy

那么这两个注解，从源码层面是如何实现的呢？



# InitDestroyAnnotationBeanPostProcessor

