package com.anxiaole.easyopen.interceptors.xss;

import com.gitee.easyopen.ApiMeta;
import com.gitee.easyopen.interceptor.ApiInterceptorAdapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiuXianfa
 * 
 * @date 11/20 23:13
 */
public class XssInterceptor extends ApiInterceptorAdapter {

    private final static HTMLFilter htmlFilter = new HTMLFilter();

    private static final String NB_PACKAGE = "com.anxiaole";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object serviceObj, Object argu) throws Exception {
        logger.info("XssInterceptor - pre handler arguClass:{}", argu.getClass().getName());

        if (argu instanceof Map) {
            filterMap((Map) argu);
        } else if (argu.getClass().getName().contains(NB_PACKAGE)) {
            // 参数是 com.anxiaole.*.Xxx
            filterFields(argu, argu.getClass().getDeclaredFields());
        }

        return true;
    }

    private void filterMap(Map argu) {
        for (Object key : argu.keySet()) {
            Object value = argu.get(key);
            if (value instanceof String) {
                argu.put(key, htmlFilter.filter((String) value));
            } else if (value instanceof Map) {
                filterMap((Map) value);
            }
        }
    }

    private void filterFields(Object argu, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(argu);
            if (fieldValue instanceof String) {
                field.set(argu, htmlFilter.filter((String) fieldValue));
            } else if (fieldValue != null && fieldValue.getClass().getName().contains(NB_PACKAGE)) {
                // entity实体中存在 com.anxiaole.*.Xxx 类型的字段.
                filterFields(fieldValue, fieldValue.getClass().getDeclaredFields());
            } else if (fieldValue instanceof List) {
                // entity实体中存在java.util.List类型的字段.
                Object list = ((List) fieldValue).stream()
                                                 .map(filterElement())
                                                 .collect(Collectors.toList());
                field.set(argu, list);
            } else if (fieldValue instanceof Set) {
                // entity实体中存在java.util.Set类型的参数
                Object set = ((Set) fieldValue).stream()
                                               .map(filterElement())
                                               .collect(Collectors.toSet());
                field.set(argu, set);
            }
        }
    }

    private Function filterElement() {
        return elementInList -> {
            if (elementInList instanceof String) {
                // entity实体中存在Set<String>类型的字段.
                return htmlFilter.filter((String) elementInList);
            } else if (elementInList.getClass().getName().contains(NB_PACKAGE)) {
                // entity实体中存在Set<com.anxiaole.*.Xxx>类型的字段.
                try {
                    filterFields(elementInList, elementInList.getClass().getDeclaredFields());
                } catch (IllegalAccessException e) {
                    logger.warn("字段非法访问:{}", e.getMessage());
                }
                return elementInList;
            } else if (elementInList instanceof Map) {
                filterMap(((Map) elementInList));
            }
            return elementInList;
        };
    }

    @Override
    public boolean match(ApiMeta apiMeta) {
        return apiMeta.getHandler().getClass().getAnnotation(UseXssInterceptor.class) != null ||
                apiMeta.getMethod().getAnnotation(UseXssInterceptor.class) != null;
    }
}
