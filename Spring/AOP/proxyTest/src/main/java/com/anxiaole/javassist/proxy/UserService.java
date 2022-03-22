package com.anxiaole.javassist.proxy;

/**
 * @author LiuXianfa
 * 
 * @date 4/26 14:19
 */
public class UserService {
    public User getUserById(Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("我的name是" + id);
        return user;
    }
}
