

# jdk动态代理的一个小Demo

`jdk动态代理`必须要求`目标对象`实现接口。具体原因后面会讲。这里先写一个jdk动态代理的使用方法：

定义一个接口：

```java
public interface LoginService {
    void login(String userName, String pwd);
}
```

实现类：

```java
public class LoginServiceImpl implements LoginService {
    @Override
    public void login(String userName, String pwd) {
        System.out.println("当前登录用户名为:" + userName +"。登录成功，欢迎使用本系统。");
    }
}
```

代理逻辑：

```java
public class InvocationHandlerImpl implements InvocationHandler {
    private Object targetObject; // 目标对象

    public InvocationHandlerImpl(Object targetObject) {
        this.targetObject = targetObject;
    }
    
    // 在目标方法执行前后，打印日志。
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before"); 
        Object result = method.invoke(this.targetObject, args);  // 使用反射调用目标方法
        System.out.println("after");
        return result;
    }
}
```

> 注意：`invoke`方法的第一个参数`proxy`，是在应用程序运行期创建的`代理对象`。使用反射调用目标方法时，不能使用`proxy`对象。
>
> 目标对象，应该是`InvocationHandlerImpl`类中的一个`成员变量`，也就是这里的 `Object targetObject` 。

测试方法：

```java
    public static void main(String[] args) throws Exception {
        LoginServiceImpl targetObject = new LoginServiceImpl();
        InvocationHandlerImpl invocationHandlerImpl = new InvocationHandlerImpl(targetObject);
        LoginService proxy = (LoginService) Proxy.newProxyInstance(
                                                     targetObject.getClass().getClassLoader(), 
                                                     targetObject.getClass().getInterfaces(), 
                                                     invocationHandlerImpl)

        proxy.login("张三", "123456"); // 调用代理对象的方法。
    }
```

控制台输出：

```
Before
当前登录用户名为:张三。登录成功，欢迎使用本系统。
after
```

**在调用`proxy`代理对象的方法时，会直接调用 `InvocationHandlerImpl#invoke` 方法。**

接下来我们主要讲解jdk动态代理的原理。

# 原理讲解

我们写的`main`方法中，`proxy.login("张三", "123456");` 点进去，只会看到`LoginServiceImpl`的逻辑，那为什么调用代理对象的方法的时候，会调用`InvocationHandler#invoke`中的方法呢？

jdk动态代理原理是：在**运行期**，动态的创建`代理类`，我们可以使用下面一行代码，把生成的代理类class文件输出出来：

```java
// 把jdk动态代理生成的代理类class文件,输出到 [当前项目跟目录+包名] 目录里.
System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
// 详见:sun.misc.ProxyGenerator#saveGeneratedFiles
```

添加完这一行代码，`main`方法如下：

```java
public static void main(String[] args) throws Exception {
    // 把jdk动态代理生成的代理类class文件,输出到 [当前项目跟目录+包名] 目录里.
    System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
    
    LoginServiceImpl targetObject = new LoginServiceImpl();
    InvocationHandlerImpl invocationHandlerImpl = new InvocationHandlerImpl(targetObject);
    LoginService proxy = (LoginService) Proxy.newProxyInstance(
                                                 targetObject.getClass().getClassLoader(), 
                                                 targetObject.getClass().getInterfaces(), 
                                                 invocationHandlerImpl)

    proxy.login("张三", "123456"); // 调用代理对象的方法。
}
```

此时再次执行，你就会在  [当前项目跟目录+包名] 目录里发现了几个class类，这里我们生成的`代理类class`反编译后的内容：*（有删减）*

```java
 import com.service.LoginService;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.lang.reflect.UndeclaredThrowableException;

 public final class LoginServiceImpl$Proxy extends Proxy implements LoginService {
     
     // 使用反射从 com.service.LoginService 接口中获得login方法。
     private static Method m3 = Class.forName("com.service.LoginService")
                   .getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));

     public LoginServiceImpl$Proxy(InvocationHandler var1) throws  {
         super(var1);
     }

     public final void login(String var1, String var2) throws  {
         try {
             // 动态生成的代理类中的代理方法,会直接调用{@link InvocationHandler#invoke}方法.
             super.h.invoke(this, m3, new Object[]{var1, var2});   
         } catch (RuntimeException | Error var4) {
             throw var4;
         } catch (Throwable var5) {
             throw new UndeclaredThrowableException(var5);
         }
     }
     
     // 省略 Object类中的三个方法，主要看login方法。
 }
```

根据上面运行期生成的代理类反编译代码，就解答我们上面的疑问：为什么调用代理对象的方法的时候，会调用`InvocationHandler#invoke`中的方法呢？

在`login`方法中，直接调用 `super.h.invoke(this, m3, new Object[]{var1, var2});`   方法，那么`super.h` 其实是`Proxy`类中的属性：

![image-20210731013733759](images/image-20210731013733759.png)

而我们的代理逻辑`InvocationHandlerImpl`就是`InvocationHandler`的子类，也就是在代理类的`login`方法中，直接调用了`InvocationHandlerImpl#login`方法。



> 画外音：
>
> 对于jdk动态代理的原理我们先讲到这里。其实还有 `java.lang.reflect.Proxy#newProxyInstance` 方法执行流程还没有讲解，
>
> 但是到这里其实就基本理解了jdk动态代理了。
>
> 
>
> 对于  `java.lang.reflect.Proxy#newProxyInstance` 方法执行流程详解：[Proxy#newProxyInstance](#Proxy#newProxyInstance)





# Proxy#newProxyInstance

> 理解这个方法确实有点困难，如果有时间、有兴趣的伙伴可以研究研究此章节。
>
> 否则可以直接看下一章节：[总结](#总结)

```java
public class Proxy implements java.io.Serializable {
  /** 
    * parameter types of a proxy class constructor 代理类构造方法的参数列表 
    * 
    * 从反编译的代理类中看到构造器方法参数是固定的:
    *    public LoginServiceImpl$Proxy(InvocationHandler var1) throws  {
    *        super(var1);
    *    }
    */
    private static final Class<?>[] constructorParams = { InvocationHandler.class };

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) {
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         * 1、查找或生成指定的代理类——在第一次生成代理类之后，会缓存起来。
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        // 2、调用构造方法，实例化对象。
        try {
            if (sm != null) { // todo：？？？？？？？？
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }

            // 获得代理类的构造方法。构造方法的参数为：  InvocationHandler.class
            final Constructor<?> cons = cl.getConstructor(constructorParams);
            // 如果构造方法不是public的，就给构造器设置可访问。
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            // 构造方法，实例化对象。
            return cons.newInstance(new Object[]{h});
        } catch (Exception e) {
            // 忽略异常处理。。。。
        }
    }
}    
```





















# 总结

1、使用`Proxy.newProxyInstance()`静态方法创建代理对象，需要传入三个参数:
     1）类加载器——因为会在运行期动态生成代理类，会使用此类加载器加载生成的代理类。
     2）目标对象的接口数组——备注:直接使用 `target.getClass().getInterfaces()` 就行
     3）实现`InvocationHandler#invoke`方法的子类.
2、使用 `System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");` 会把代理类的class文件输出到 [项目根目录+包名]文件夹中。
      备注:反编译代理类可以看到，代理类中所有方法都会去调用 `InvocationHandler#invoke` 方法。
3、调用代理对象proxy的任何方法都会被拦截，直接调用target目标对象的任何方法，都不会被拦截。
4、为什么jdk动态代理,必须实现接口?   阿里P7
     答：从反编译的代理中上我们看到：`代理类`已经继承了`java.lang.reflect.Proxy`类，而在java中类只能`单继承`，所以只能要求[被代理类target]实现接口.
5、jdk动态代理的优点:
       依赖jdk本身的api，无需引入其他jar包.
6、jdk动态代理的缺点:
       必须要求需要代理的类实现自接口.对代码有侵入性.
       性能方面,经过jdk8优化后，已经和cglib差不多了.
7、spring官方用的jdk动态代理实现的aop源码: org.springframework.aop.framework.JdkDynamicAopProxy





# Spring+jdk动态代理

> 这个demo结合了一些spring源码，所以看这会有点绕。不过仔细看，还是能理解的。
>
> 但并不是spring中使用jdk动态代理的所有内容，只是为了阅读spring的一个入门级小demo。
>
> 其实还是建议：把jdk动态代理的内容先理解了，再开始看spring aop。否则很痛苦的。

测试用例详见：[com.atguigu.test.jdkDynamicProxy.MyJdkDynamicProxyTest](https://gitee.com/anxiaole/spring-framework/blob/5.0.x/spring-example/src/test/java/com/atguigu/test/jdkDynamicProxy/MyJdkDynamicProxyTest.java)

![image-20210329231131350](images/image-20210329231131350.png)















