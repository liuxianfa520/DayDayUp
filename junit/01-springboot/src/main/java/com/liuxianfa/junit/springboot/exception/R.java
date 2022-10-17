package com.liuxianfa.junit.springboot.exception;

import org.springframework.lang.Nullable;

import java.io.Serializable;

import lombok.Data;

@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private T data;
    private String msg;

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static boolean isSuccess(@Nullable R<?> result) {
        if (result == null) {
            return false;
        }
        return result.getCode() == 200;
    }

    public static boolean isNotSuccess(@Nullable R<?> result) {
        return !isSuccess(result);
    }

    public static <T> R<T> data(T data) {
        return data(data, "操作成功");
    }

    public static <T> R<T> data(T data, String msg) {
        return data(200, data, msg);
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R(code, data, data == null ? "暂无承载数据" : msg);
    }


    public static <T> R<T> fail(String msg) {
        return new R(500, null, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R(code, (Object) null, msg);
    }
}
