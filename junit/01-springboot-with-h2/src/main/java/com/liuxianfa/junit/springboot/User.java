package com.liuxianfa.junit.springboot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Table
@Accessors(chain = true)
public class User {

    @Id
    private Long id;

    String name;

    int age;
}
