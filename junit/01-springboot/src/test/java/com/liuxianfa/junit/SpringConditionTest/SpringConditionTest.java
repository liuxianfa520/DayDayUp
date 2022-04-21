package com.liuxianfa.junit.SpringConditionTest;

import com.liuxianfa.junit.springboot.SpringbootApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.test.context.junit4.SpringRunner;

import cn.hutool.extra.spring.SpringUtil;

/**
 * @date 2022/4/21 16:37
 */
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(value = SpringRunner.class)
public class SpringConditionTest {


    @Test
    public void name() {
        String placeHolderCondition = "'${spring.application.name}' == '01-springboot'";

        String expressionString = SpringUtil.getApplicationContext().getEnvironment().resolvePlaceholders(placeHolderCondition);
        System.out.println(expressionString);

        Object value = new SpelExpressionParser().parseExpression(expressionString).getValue();
        System.out.println(value);
    }
}
