package com.pandora.storage.es.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 返回数据
 * 泛型 {@link T} 表示 {@link R#getData()} 的类型
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker
 */
public class R<T> extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String error) {
        R r = new R();
        r.put("code", code);
        r.put("error", error);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static <T> R data(T data) {
        return new R().put("data", data);
    }

    public static boolean isSuccess(R r) {
        return r != null && r.getCode() == 0;
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public T getData() {
        return (T) get("data");
    }

    public int getCode() {
        return get("code") == null ? 0 : (int) get("code");
    }

    public String getError() {
        return get("error") == null ? null : get("error").toString();
    }

    public boolean isSuccess() {
        return R.isSuccess(this);
    }

    public String getApplicationName() {
        Object o = get("applicationName");
        return o == null ? "unknow" : o.toString();
    }

    public R setApplicationName(String applicationName) {
        return put("applicationName", applicationName);
    }
}
