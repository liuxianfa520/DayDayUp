package com.anxiaole.multitenancy.exception;

import lombok.Data;

/**
 * @author LiuXianfa
 * 
 * @date 4/10 22:55
 */
@Data
public class CreateDataSourceException extends RuntimeException {

    private static final String default_msg = "创建租户数据源失败!租户id:[%s]";
    private String tenantId;

    public CreateDataSourceException(String tenantId, Throwable cause) {
        super(String.format(default_msg, tenantId), cause);
    }

    public CreateDataSourceException(String tenantId) {
        super(String.format(default_msg, tenantId));
    }
}
