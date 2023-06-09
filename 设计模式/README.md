# 设计模式七大原则

1. 单一职责原则
2. 接口隔离原则
   1. 一个类对另一个类的依赖应该建立在最小的接口上。
   2. 简单来说，我们在设计接口时，不要设计出庞大臃肿的接口，因为实现这种接口时需要实现很多不必要的方法。我们要尽量设计出功能单一的接口，这样也能保证实现类的职责单一。
3. 依赖倒转（倒置）原则
   1. 系统的高层模块不应该依赖低层模块的具体实现，二者都应该依赖其抽象类或接口，抽象接口不应该依赖具体实现类，而具体实现类应该于依赖抽象。
   2. 简单来说，我们要面向接口编程。当需求发生变化时对外接口不变，只要提供新的实现类即可。
4. 里氏替换原则
   1. 在使用继承时，子类尽量不要重写父类的方法。
   2. 在适当情况下可以使用依赖，聚合，组合来解决上述情况。
   3. 里氏替换原则告诉我们，继承实际上让两个类耦合性增强了。
5. 开闭原则 Open Closed Principle
   1. 一个软件实体（比如模块、类、函数等）应该**对扩展开放，对修改关闭。** 
   2. 简单来说，当需求发生变化时，我们可以通过添加新的模块满足新需求，而不是通过修改原来的实现代码来满足新需求。
   3. **在这六条原则中，开放-封闭原则是最基;础的原则，也是其他原则以及后文介绍的所有设计模式的最终目标。**
6. 迪米特法则
   1. 又称为：最少知道原则
   2. 每个类都会与其他类存在耦合关系。但是一个类依赖的其他类尽量不要只出现在局部变量中。（最少依赖，只依赖直接朋友）
7. *合成复用原则
   1. 尽量使用合成/聚合的方式，而不是使用继承。
   2. HAS-A   优于    IS-A
8. 设计原则核心思想
   1. 找出应用中可能变化的地方，把它们独立出来，不要和那些不变的代码混在一起。
   2. 针对接口编程，而不是针对实现编程。 
   3. 为了交互对象之间的松耦合设计而努力。







此目录下的内容全部来自: https://github.com/spring2go/core-spring-patterns

设计模式
======

Spring核心原理和模式，波波微课

## 大纲

### 1. 创建模式

名称 | 代码 | ppt | 视频 |应用
----|-----|-----|-----|-----
简单工厂Simple Factory|[code](patterns/creation/simple-factory)|[ppt](ppts/简单工厂.pdf)|[video](https://v.qq.com/x/page/n0629exrd31.html)|
工厂方法Factory Method|[code](patterns/creation/factory-method)|[ppt](ppts/工厂方法.pdf)|[video](https://v.qq.com/x/page/a0629kh0xxc.html)|
抽象工厂Abstract Factory|[code](patterns/creation/abstract-factory)|[ppt](ppts/抽象工厂.pdf)|[video](https://v.qq.com/x/page/p0629psr89t.html)|
单例Singleton|[code](patterns/creation/singleton)|[ppt](ppts/单例.pdf)|[video](https://v.qq.com/x/page/s0630rqg9wg.html)|
构造者Builder|[code](patterns/creation/builder)|[ppt](ppts/构建者.pdf)|[video](https://v.qq.com/x/page/z0630bg1qs3.html)|org.apache.ibatis.session.SqlSessionFactoryBuilder<br />org.apache.ibatis.builder.xml.XMLConfigBuilder<br />org.apache.ibatis.builder.xml.XMLMapperBuilder
流畅接口Fluent Interface|[code](patterns/creation/builder)|[ppt](ppts/流畅接口.pdf)|[video](https://v.qq.com/x/page/v063000n872.html)|

### 2. 结构模式

名称 | 代码 | ppt | 视频 |应用
----|-----|-----|-----|-----
代理Proxy|[code](patterns/structural/proxy)|[ppt](ppts/代理模式.pdf)|[video](https://v.qq.com/x/page/k0637354wuw.html)|org.apache.ibatis.session.SqlSessionManager#sqlSessionProxy<br />org.mybatis.spring.SqlSessionTemplate#sqlSessionProxy<br />org.apache.ibatis.plugin.Plugin#wrap   MyBatis插件实现
桥接Bridge|[code](patterns/structural/bridge)|[ppt](ppts/桥接器.pdf)|[video](https://v.qq.com/x/page/h0632obkktb.html)|[java.sql.DriverManager](https://www.cnblogs.com/yougewe/p/12460685.html)<br />[slf4j](https://zhuanlan.zhihu.com/p/317095958)
装饰Decorator|[code](patterns/structural/decorator)|[ppt](ppts/装饰模式.pdf)|[video](http://v.qq.com/x/page/p0636w3d6s2.html)|org.apache.ibatis.cache.Cache<br />org.apache.ibatis.executor.CachingExecutor
适配器Adapter|[code](patterns/structural/adapter)|[  ](ppts/适配器.pdf)|[video](https://v.qq.com/x/page/w0632njvzkw.html)|org.springframework.web.servlet.HandlerAdapter<br />org.apache.ibatis.logging.Log
门面Facade|[code](patterns/structural/facade)|[ppt](ppts/门面模式.pdf)|[video](https://v.qq.com/x/page/m06379lgexy.html)|org.apache.ibatis.session.SqlSession<br />[slf4j](https://gitee.com/anxiaole/DayDayUp/blob/master/%E6%97%A5%E5%BF%97/SLF4J%EF%BC%9AClass%20path%20contains%20multiple%20SLF4J%20bindings.md)
组合Composite|[code](patterns/structural/composite)|[ppt](ppts/组合模式.pdf)|[video](https://v.qq.com/x/page/t0634x9lbew.html)|[org.apache.ibatis.scripting.xmltags.MixedSqlNode](https://gitee.com/anxiaole/DayDayUp/tree/master/设计模式/装饰模式和组合模式.md)


### 3. 行为模式

名称 | 代码 | ppt | 视频 |应用
----|-----|-----|-----|-----
职责链Chain of Responsibility|[code](patterns/behavior/chain_of_responsibility)|[ppt](ppts/职责链模式.pdf)|[video](http://v.qq.com/x/page/r0640omm9hs.html)|
命令Command|[code](patterns/behavior/command)|[ppt](ppts/命令模式.pdf)|[video](https://v.qq.com/x/page/j0641ba2m4j.html)|
解释器Interpreter|[code](patterns/behavior/interpreter)|[ppt](ppts/解释器模式.pdf)|[video](http://v.qq.com/x/page/t0642g9ioam.html)|[org.apache.ibatis.scripting.xmltags.XMLScriptBuilder.NodeHandler](https://www.bilibili.com/video/BV1Tp4y1X7FM?p=37)
迭代器Iterator|[code](patterns/behavior/iterator)|[ppt](ppts/迭代器模式.pdf)|[video](https://v.qq.com/x/page/a0643uhcvgn.html)|
策略Strategy|[code](patterns/behavior/strategy)|[ppt](ppts/策略模式.pdf)|[video](http://v.qq.com/x/page/n0645457b19.html)|
观察者Observer|[code](patterns/behavior/observer)|[ppt](ppts/观察者模式.pdf)|[video](http://v.qq.com/x/page/j0653cekdal.html)|
模板方法Template Method|[code](patterns/behavior/template)|[ppt](ppts/模板方法模式.pdf)|[video](https://v.qq.com/x/page/p0654hjkpy3.html)|

### 4. 核心模式

名称 | 代码 | ppt | 视频 |
----|-----|-----|-----|
依赖倒置原则，控制反转，依赖注入|[code](patterns/general/dependency-inversion)|[ppt](ppts/DIP+IoC+DI.pdf)|[video](https://v.qq.com/x/page/k0629qsrpz5.html)|

## 波波微课公众号

![公众号](image/qrcode_wechat.jpg)

## 官网

[jskillcloud.com](http://www.jskillcloud.com)







# 其他

## 代理模式、桥接模式、装饰器模式、适配器模式4种设计模式的区别

**区别**
代理、桥接、装饰器、适配器，这 4 种模式是比较常用的结构型设计模式。它们的代码结构非常相似。笼统来说，它们都可以称为 Wrapper 模式，也就是通过 Wrapper 类二次封装原始类。

尽管代码结构相似，但这 4 种设计模式的用意完全不同，也就是说要解决的问题、应用场景不同，这也是它们的主要区别。这里我就简单说一下它们之间的区别。

**代理模式**：代理模式在不改变原始类接口的条件下，为原始类定义一个代理类，主要目的是控制访问，而非加强功能，这是它跟装饰器模式最大的不同。

**桥接模式**：桥接模式的目的是将接口部分和实现部分分离，从而让它们可以较为容易、也相对独立地加以改变。

**装饰器模式**：装饰者模式在不改变原始类接口的情况下，对原始类功能进行增强，并且支持多个装饰器的嵌套使用。

**适配器模式**：适配器模式是一种事后的补救策略。适配器提供跟原始类不同的接口，而代理模式、装饰器模式提供的都是跟原始类相同的接口。





## 装饰者模式和组合模式的区别？

[点击跳转](https://gitee.com/anxiaole/DayDayUp/tree/master/设计模式/装饰模式和组合模式.md)