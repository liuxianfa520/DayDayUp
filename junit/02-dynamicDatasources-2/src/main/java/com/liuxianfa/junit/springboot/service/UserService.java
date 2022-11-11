package com.liuxianfa.junit.springboot.service;

import com.liuxianfa.junit.springboot.user.dao.TUserMapper;
import com.liuxianfa.junit.springboot.user.entity.TUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    TUserMapper userMapper;

    /**
     * 事务传播特性:不需要事务
     */
    @Transactional(propagation = Propagation.NEVER)
    public TUser getById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }
}