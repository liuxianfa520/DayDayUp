import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * https://blog.csdn.net/qq_33101675/article/details/108437777
 *
 * @date 2022/4/24 10:24
 */
public class SpringELTest {

    public static String fun() {
        return "这是一个自定义函数";
    }

    @Data
    @AllArgsConstructor
    static
    class User {
        String name;
        Integer age;
    }

    @Test
    @SneakyThrows
    public void test1() {
        String expressionString = "('Hello' + ' World').concat(#end).concat('Hello,' + #root.name + '!').concat(#myFunction())";

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionString);
        StandardEvaluationContext context = new StandardEvaluationContext(new User("安小乐", 18));
        // 自定义函数,使用 #myFunction()
        context.registerFunction("myFunction", SpringELTest.class.getMethod("fun"));
        context.setVariable("end", "!");
        System.out.println(expression.getValue(context));
    }


    static ArrayList<User> userList = new ArrayList<User>() {{
        add(new User("安小乐", 18));
        add(new User("张三丰", 19));
        add(new User("李易峰", 20));
    }};

    public static String getUserByAge(Integer age) {
        return userList.stream().filter(user -> user.getAge().equals(age)).findFirst().orElse(new User("暂无此人", -1)).getName();
    }

    @Test
    @SneakyThrows
    public void registerFunction() {
        String expressionString = "('Hello World!').concat('Hello,' + #getUserByAge(19) + '!')";

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionString);
        StandardEvaluationContext context = new StandardEvaluationContext(new User("安小乐", 18));
        // 自定义函数,使用 #myFunction()
        context.registerFunction("getUserByAge", SpringELTest.class.getMethod("getUserByAge", Integer.class));
        System.out.println(expression.getValue(context));
    }
}
