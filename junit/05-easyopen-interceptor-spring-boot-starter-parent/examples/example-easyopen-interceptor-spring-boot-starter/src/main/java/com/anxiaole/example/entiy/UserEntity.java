package com.anxiaole.example.entiy;

import com.gitee.easyopen.doc.annotation.ApiDocField;

import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 11/25 20:33
 */
@Data
public class UserEntity {

    @ApiDocField(name = "name", description = "名称", example = "")
    private String name;

    @ApiDocField(name = "empEntity", description = "员工实体", beanClass = EmpEntity.class)
    private EmpEntity empEntity;

}
