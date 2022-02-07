Spring创建bean流程

```
prepareEnvironment();//获取设置配置文件（.yml等）
invokeBeanFactoryPostProcessors();//解析配置文件，生成所有需要的beanDefinitin;
//初始化非懒加载beanDefinition
finishBeanFactoryInitialization(){
	Iterator var2 = beanNames.iterator();//遍历所有的beanDefinition
	getBean(var2.next);
}

//关键！：这里是初始化一个bean的过程
getBean(beanName) {
	doGetBean();//这里递归处理依赖类
	createBeanInstance();//反射创建实例
	populateBean();//填充bean属性，包含依赖类
	invokeAwareMethods();//如果bean实现了某个Aware接口，就会调用它的方法
	//递归调用所有的后置处理器的 before 方法，其中通过CommonAnnotationPostProcessor调用了@PostConstruct指定的初始化方法
	applyBeanPostProcessorsBeforeInitialization();
	invokeInitMethods();//调用实现InitializingBean的afterPropertySet()方法，然后调用@Bean(initMethod="..")指定的方法
	applyBeanPostProcessorsAfterInitialization();//递归调用所有的后置处理器的 after 方法
}
```

