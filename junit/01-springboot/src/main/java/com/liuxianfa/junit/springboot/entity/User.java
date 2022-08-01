package com.liuxianfa.junit.springboot.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @date 2022/8/1 9:55
 */
@Data
@Accessors(chain = true)
public class User {

    Integer id;

    String name;
}
