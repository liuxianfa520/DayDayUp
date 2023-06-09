package com.anxiaole.jdk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <pre>
 * 测试&学习jdk动态代理
 * 1、使用Proxy.newProxyInstance()方法生成代理对象,传入三个参数:
 *    1)类加载器
 *    2)目标对象的接口数组
 *      备注:直接使用 target.getClass().getInterfaces() 就行
 *    3)实现InvocationHandler的invoke方法.
 * 2、调用proxy代理对象的任何方法都会被增强;直接调用target目标对象的任何方法,都不会被增强.
 * 3、动态生成的代理类class文件: 在[项目根目录+包名]文件夹中.(执行一次main方法,才会生成)(目前不知道如何修改成其他目录.)
 *      备注:在生成的代理对象class文件中可以看到,代理对象内部所有方法都会去调用 {@link InvocationHandler#invoke} 方法.
 * {@code
 * import com.atguigu.test.jdkProxy.LoginService;
 * import java.lang.reflect.InvocationHandler;
 * import java.lang.reflect.Method;
 * import java.lang.reflect.Proxy;
 * import java.lang.reflect.UndeclaredThrowableException;
 *
 * public final class LoginServiceImpl$Proxy extends Proxy implements LoginService {
 *     private static Method m1;
 *     private static Method m2;
 *     private static Method m3;
 *     private static Method m0;
 *
 *     public LoginServiceImpl$Proxy(InvocationHandler var1) throws  {
 *         super(var1);
 *     }
 *
 *     public final boolean equals(Object var1) throws  {
 *         try {
 *             return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
 *         } catch (RuntimeException | Error var3) {
 *             throw var3;
 *         } catch (Throwable var4) {
 *             throw new UndeclaredThrowableException(var4);
 *         }
 *     }
 *
 *     public final String toString() throws  {
 *         try {
 *             return (String)super.h.invoke(this, m2, (Object[])null);
 *         } catch (RuntimeException | Error var2) {
 *             throw var2;
 *         } catch (Throwable var3) {
 *             throw new UndeclaredThrowableException(var3);
 *         }
 *     }
 *
 *     public final void login(String var1, String var2) throws  {
 *         try {
 *             super.h.invoke(this, m3, new Object[]{var1, var2});   // 动态生成的代理类中的代理方法,会直接调用{@link InvocationHandler#invoke}方法.
 *         } catch (RuntimeException | Error var4) {
 *             throw var4;
 *         } catch (Throwable var5) {
 *             throw new UndeclaredThrowableException(var5);
 *         }
 *     }
 *
 *     public final int hashCode() throws  {
 *         try {
 *             return (Integer)super.h.invoke(this, m0, (Object[])null);
 *         } catch (RuntimeException | Error var2) {
 *             throw var2;
 *         } catch (Throwable var3) {
 *             throw new UndeclaredThrowableException(var3);
 *         }
 *     }
 *
 *     static {
 *         try {
 *             m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
 *             m2 = Class.forName("java.lang.Object").getMethod("toString");
 *             m3 = Class.forName("com.atguigu.test.
 *             jdkProxy.LoginService").getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
 *             m0 = Class.forName("java.lang.Object").getMethod("hashCode");
 *         } catch (NoSuchMethodException var2) {
 *             throw new NoSuchMethodError(var2.getMessage());
 *         } catch (ClassNotFoundException var3) {
 *             throw new NoClassDefFoundError(var3.getMessage());
 *         }
 *     }
 * }
 * }
 * 4、为什么jdk动态代理,必须实现接口?   阿里P7
 *    答:JAVA语法要求[类是单继承、多实现].
 *       从上面第三条看到:代理的对象的类已经继承了java.lang.reflect.Proxy类,所以只能要求[被代理类target]实现接口.
 * 5、jdk动态代理的优点:
 *     依赖jdk本身的api.无需引入其他jar包.
 * 6、jdk动态代理的缺点:
 *    必须要求需要代理的类实现自接口.对代码有侵入性.
 *    性能方面,经过jdk8优化后,已经和cglib差不多了.
 *    官方文档对性能方面的描述:There is little performance difference between CGLIB proxying and dynamic proxies.
 *                            Performance should not be a decisive consideration in this case.
 *                   文档地址:https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-api-proxying-class
 * 7、spring官方用的jdk动态代理实现的aop源码: org.springframework.aop.framework.JdkDynamicAopProxy
 * </pre>
 *
 * @author LiuXianfa
 * 
 * @date 2/9 14:08
 */
public class JdkDynamicProxyTest implements InvocationHandler {

    public static void main(String[] args) {
        // 把jdk动态代理生成的代理类class文件,写到 [当前项目跟目录+包名] 目录里.
        // 详见:sun.misc.ProxyGenerator#saveGeneratedFiles
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");


        // 被代理的原始对象,也可以成为目标对象
        LoginServiceImpl targetObject = new LoginServiceImpl();
        JdkDynamicProxyTest jdkDynamicProxyTest = new JdkDynamicProxyTest(targetObject);
        LoginService proxy = (LoginService) Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(), jdkDynamicProxyTest);

        proxy.login("lxf", "asdf");
    }


    private Object targetObject;

    public JdkDynamicProxyTest(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke;
        try {
            System.out.println("@Before"); // 前置增强
            invoke = method.invoke(targetObject, args); // 注意这里invoke方法第一个参数,使用的是目标对象target,而不是proxy对象.如果使用proxy会报错
            System.out.println("@AfterReturning"); // 在目标方法返回之后增强
        } catch (Exception e) {
            System.err.println("@AfterThrowing         Exception:" + e.getMessage()); // 在目标方法异常时的增强
            throw e;// 如果目标方法调用异常,需要把异常抛出去.
        } finally {
            System.out.println("@After"); // 后置增强
        }
        return invoke;
    }
}

interface LoginService {
    void login(String userName, String pwd);
}


class LoginServiceImpl implements LoginService {

    @Override
    public void login(String userName, String pwd) {
        System.out.println("当前登录用户名为:" + userName);
    }
}
