package com.liuxianfa.junit.req;

import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserReq {

    @NotBlank(message = "名称不能为空")
    @ApiDocField(name = "name", description = "名称", dataType = DataType.STRING, required = true, example = "张三")
    private String name;

    @NotNull(message = "年龄不能为空")
    @ApiDocField(name = "age", description = "年龄", dataType = DataType.INT, required = true, example = "14")
    private Integer age;

}
