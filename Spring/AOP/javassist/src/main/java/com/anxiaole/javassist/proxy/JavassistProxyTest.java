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

        UserService target = new UserService();
        ProxyFactory enhancer = new ProxyFactory();
        // 把生成的代理类class文件写此目录中:
        enhancer.writeDirectory = ".\\Spring\\AOP\\javassist\\src\\main\\java";
        enhancer.setSuperclass(target.getClass());

        MethodHandler methodHandler = (self, thisMethod, proceed, methodArgs) -> {
            System.out.println("before");
            Object invoke = thisMethod.invoke(target, methodArgs);
            System.out.println("after");
            return invoke;
        };
        Object proxy = enhancer.create(new Class[]{}, new Class[]{});
        ((Proxy) proxy).setHandler(methodHandler);

        System.out.println("代理对象类名:" + proxy.getClass().getName());
        User user = ((UserService) proxy).getUserById(1);
        System.out.println(JSON.toJSONString(user, true));
    }
}