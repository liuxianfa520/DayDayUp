org.springframework.beans.factory.InitializingBean




- [bean的生命周期](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle)





Internally, the Spring Framework uses BeanPostProcessor implementations to process any callback interfaces it can find and call the appropriate methods. If you need custom features or other lifecycle behavior Spring does not by default offer, you can implement a BeanPostProcessor yourself. For more information, see [Container Extension Points](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension).