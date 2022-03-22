package com.anxiaole.easyopen;

import com.anxiaole.easyopen.interceptors.apipermission.UseApiPermissionInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import lombok.extern.log4j.Log4j;

/**
 * @author LiuXianfa
 * 
 * @date 12/10 18:59
 */
@Log4j
public class AnnotationTest {


    @UseApiPermissionInterceptor(expression = "arg.name=='lxf'", apiName = "csc.xxx.detail")
    public void aaa(String fasdfasdfasfasdf) {

    }

    public static void main(String[] args) throws NoSuchMethodException {
        // 目标方法
        Method[] methods = AnnotationTest.class.getMethods();
        long count = Arrays.stream(methods)
                         .filter(method -> method.getName().equalsIgnoreCase("aaa"))
                         .count();


        if (count == 1) {
            Optional<Method> aaa = Arrays.stream(methods)
                                         .filter(method -> method.getName().equalsIgnoreCase("aaa"))
                                         .findFirst();
            Method method = aaa.get();
            Arrays.stream(method.getParameters())
                  .map(parameter -> parameter.getName())
                  .forEach(System.out::println);

        }


//        Method method = AnnotationTest.class.getMethod("aaa");

//
//        // 目标方法的参数
//        HashMap<String, String> methodArg = new HashMap<String, String>() {{
//            put("name", "lxf");
//            put("age", "20");
//        }};
//
//
//        UseApiPermissionInterceptor annotation = method.getAnnotation(UseApiPermissionInterceptor.class);
//        String apiName = annotation.apiName();
//        String expression = annotation.expression();
//        if (!expression.isEmpty()) {
//
//            ScriptEngine scriptEngine = ScriptEngineUtil.getScriptEngine();
//            Bindings bind = new SimpleBindings();
//
//            bind.put("arg", methodArg);
//
//
//            try {
//                Boolean result = (Boolean) scriptEngine.eval(expression, bind);
//                System.out.println("表达式执行是否成功:" + result);
//                if (result) {
//                    System.out.println(String.format("去查询用户是否有%s接口权限.", apiName));
//                }
//            } catch (ScriptException e) {
//                log.error("执行脚本错误,返回false.", e);
//            }
//        }
    }
}
