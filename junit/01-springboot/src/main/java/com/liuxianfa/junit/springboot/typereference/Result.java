package com.liuxianfa.junit.springboot.typereference;

import lombok.Data;

/**
 * @author AnXiaole
 * @date 2022/8/1 9:54
 */
@Data
public class Result<T> {

    int code;

    String msg;

    T data;
}
