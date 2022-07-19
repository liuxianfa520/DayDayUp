package com.liuxianfa.junit.springboot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xianfaliu2@creditease.cn
 * @date 7/19 22:39
 */
@Repository
public interface UserMapper extends JpaRepository<User, Long> {
}
