# 依赖注入Dependency injection (DI)
- 依赖注入Dependency injection (DI)符合设计模式七大原则中的[依赖倒转原则](https://www.bilibili.com/video/BV1G4411c7N4?p=11)

- **Code is cleaner with the DI principle, and decoupling is more effective when objects are provided with their dependencies.** The object does not look up its dependencies and does not know the location or class of the dependencies. As a result, your classes become easier to test, particularly when the dependencies are on interfaces or abstract base classes, which allow for stub or mock implementations to be used in unit tests.

- **DI exists in two major variants: [Constructor-based dependency injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-constructor-injection) and [Setter-based dependency injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-setter-injection).**



# 问题
- 依赖注入符合设计模式七大原则的哪个原则?
- 依赖注入有什么有点?
- spring中，依赖注入(DI)存在哪两种变体？
- [依赖处理过程？](../CircularDependencies/CircularDependencies.md)
- [如何解决 循环依赖?](../CircularDependencies/CircularDependencies.md)

