package exception;

import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Set;

import javax.validation.ConstraintViolation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 参数校验util
 *
 * @see org.springframework.validation.annotation.Validated
 * @see javax.validation.constraints.NotEmpty
 * @see javax.validation.constraints.NotNull
 * @see javax.validation.constraints.NotBlank
 */
public class ValidatedUtils {

    public static void validate(Object t, Class<?>... groups) {
        Set<ConstraintViolation<Object>> error = SpringUtil.getBean(SpringValidatorAdapter.class).validate(t, groups);
        if (CollUtil.isNotEmpty(error)) {
            String message = error.stream().findFirst().get().getMessage();
            throw new ServiceException(message);
        }
    }
}
