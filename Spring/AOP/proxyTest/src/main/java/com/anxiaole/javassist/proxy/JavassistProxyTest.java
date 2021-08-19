package com.anxiaole.javassist.proxy;

import com.alibaba.fastjson.JSON;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 4/26 14:18
 */
public class JavassistProxyTest {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // 目标对象
        UserService target = new UserService();
        // 创建代理对象的工厂,主要用来创建代理对象的.
        ProxyFactory enhancer = new ProxyFactory();
        // 把生成的代理类class文件写此目录中:
        enhancer.writeDirectory = ".\\Spring\\AOP\\javassist\\src\\main\\java";
        enhancer.setSuperclass(target.getClass());

        // 代理逻辑
        MethodHandler methodHandler = (self, thisMethod, proceed, methodArgs) -> {
            System.out.println("before");
            Object invoke = thisMethod.invoke(target, methodArgs);// 调用目标方法
            System.out.println("after");
            return invoke;
        };
        // 创建代理对象
        Object proxy = enhancer.create(new Class[]{}, new Class[]{});
        // 给代理对象设置代理逻辑 (代理对象和代理逻辑不是强绑定的,也就是说:代理对象创建出来之后,也能修改重新修改代理逻辑MethodHandler)
        ((Proxy) proxy).setHandler(methodHandler);


        System.out.println("代理对象类名:" + proxy.getClass().getName());
        System.out.println();

        System.out.println("=============下面调用代理对象的方法===============");
        User user = ((UserService) proxy).getUserById(1);
        System.out.println();

        System.out.println("=============下面打印方法执行结果:===============");
        System.out.println(JSON.toJSONString(user, true));
    }
}