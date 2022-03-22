package com.anxiaole.example.entiy;

import com.gitee.easyopen.doc.annotation.ApiDocField;

import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 11/25 22:24
 */
@Data
public class EmpEntity {

    @ApiDocField(name = "mobile", description = "手机号", example = "")
    private String mobile;

}
