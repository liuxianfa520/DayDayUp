package com.liuxianfa.junit.SpringConditionTest;

import com.liuxianfa.junit.springboot.SpringbootApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @date 2022/4/21 16:37
 */
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class SpringConditionTest {


    @Test
    public void name() {
        String placeHolderCondition = "'${spring.application.name}' == '01-springboot'";

        // spring解析占位符
        String expressionString = SpringUtil.getApplicationContext().getEnvironment().resolvePlaceholders(placeHolderCondition);
        System.out.println(expressionString);

        Boolean eq = new SpelExpressionParser().parseExpression(expressionString).getValue(Boolean.class);
        System.out.println(eq);
    }

    @Data
    @AllArgsConstructor
    public static class User {
        String name;
        int age;
    }


    // 带有参数的表达式
    @Test
    public void name2() {
        String placeHolderCondition = "name == '张三' && age == 18 && #address == '北京' && '${spring.application.name}' == '01-springboot'";

        // spring解析占位符
        String expressionString = SpringUtil.getApplicationContext().getEnvironment().resolvePlaceholders(placeHolderCondition);
        System.out.println(expressionString);

        User rootObject = new User("张三", 18);
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(rootObject);
        // 表达式中,需要使用  #address  表示这个变量
        evaluationContext.setVariable("address", "北京");

        Boolean value = new SpelExpressionParser().parseExpression(expressionString).getValue(evaluationContext, Boolean.class);
        System.out.println(value);
    }
}
