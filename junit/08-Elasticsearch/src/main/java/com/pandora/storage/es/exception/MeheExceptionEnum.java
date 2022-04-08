package com.pandora.storage.es.exception;

import org.springframework.http.HttpStatus;

/**
 * <pre>
 *     响应格式： https://leancloud.cn/docs/rest_api.html#hash670852090
 *     错误码详解：https://leancloud.cn/docs/error_code.html#hash1389225
 * </pre>
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/3 10:03
 */
public enum MeheExceptionEnum {
    NOT_FOUND(1, HttpStatus.NOT_FOUND, "Not Found."),
    // 如果以下异常code不存在，在这里添加。

    INTERNAL_SERVER_ERROR(95, "服务器内部异常！"),
    INDEX_NAME_NOT_EXIST(96, "Elasticsearch中不存在此indexName"),
    TOKEN_CANNOT_EMPTY(97, "token不能为空！"),
    APP_ID_CANNOT_EMPTY(98, "appId不能为空！"),
    INVALID_APP_ID(99, "非法的appId"),
    CONNECTION_FAILED(100, ""),
    OBJECT_NOT_FOUND(101, HttpStatus.NOT_FOUND, "Class or object doesn't exists."),
    INVALID_QUERY(102, ""),
    INVALID_CLASS_NAME(103, "className 格式不正确！"),
    MISSING_OBJECT_ID(104, ""),
    INVALID_KEY_NAME(105, ""),
    INVALID_POINTER(106, HttpStatus.BAD_REQUEST, "Malformed pointer. Pointers must be maps of a classname and an object id."),
    INVALID_JSON(107, ""),
    COMMAND_UNAVAILABLE(108, ""),
    NOT_INITIALIZED(109, ""),
    INCORRECT_TYPE(111, ""),
    INVALID_CHANNEL_NAME(112, ""),
    PUSH_MISCONFIGURED(115, ""),
    OBJECT_TOO_LARGE(116, ""),
    OPERATION_FORBIDDEN(119, ""),
    CACHE_MISS(120, ""),
    INVALID_NESTED_KEY(121, ""),
    INVALID_FILE_NAME(122, ""),
    INVALID_ACL(123, ""),
    TIMEOUT(124, ""),
    INVALID_EMAIL_ADDRESS(125, ""),
    INVALID_FILE_URL(126, ""),
    INVALID_PHONE_NUMBER(127, ""),
    DUPLICATE_VALUE(137, ""),
    INVALID_ROLE_NAME(139, ""),
    EXCEEDED_QUOTA(140, ""),
    SCRIPT_ERROR(141, ""),
    VALIDATION_ERROR(142, ""),
    FILE_DELETE_ERROR(153, ""),
    USERNAME_MISSING(200, ""),
    PASSWORD_MISSING(201, ""),
    USERNAME_TAKEN(202, ""),
    EMAIL_TAKEN(203, ""),
    EMAIL_MISSING(204, ""),
    EMAIL_NOT_FOUND(205, ""),
    SESSION_MISSING(206, ""),
    MUST_CREATE_USER_THROUGH_SIGNUP(207, ""),
    ACCOUNT_ALREADY_LINKED(208, ""),
    USER_ID_MISMATCH(209, ""),
    USERNAME_PASSWORD_MISMATCH(210, ""),
    USER_DOESNOT_EXIST(211, "Could not find user."),
    USER_MOBILEPHONE_MISSING(212, ""),
    USER_WITH_MOBILEPHONE_NOT_FOUND(213, ""),
    USER_MOBILE_PHONENUMBER_TAKEN(214, ""),
    USER_MOBILEPHONE_NOT_VERIFIED(215, ""),
    LINKED_ID_MISSING(250, ""),
    INVALID_LINKED_SESSION(251, ""),
    UNSUPPORTED_SERVICE(252, ""),
    NO_EFFECT(305, "No effect on updating/deleting a document."),
    RATE_LIMITED(503, ""),
    UNKNOWN(999, ""),
    FILE_DOWNLOAD_INCONSISTENT_FAILURE(9301, ""),
    FILE_UPLOAD_FAILURE(9302, ""),
    INVALID_STATE(9303, ""),
    INVALID_PARAMETER(9304, ""),
    CIRCLE_REFERENCE(100001, ""),

    // DataOoException

    QUERY_FIELD_ERROR(100000, "强类型class，字段为空"),

    QUERY_CLASS_ERROR(100001, "未获取到class信息"),

    NO_VALUE_ERROR(100002, "对象值不能为空"),

    LENGTH_ERROR(100003, "字段超长"),

    TYPE_ERROR(100004, "字段类型不符"),

    FORMAT_ERROR(100005, "json格式不正确"),

    CREATE_CLASS_ERROR(100006, "创建class格式不正确"),

    CREATE_SAME_CLASS_ERROR(100007, "class已经存在"),

    APPID_NOT_EXIST(100008, "参数值错误：传入的appId值不存在！"),

    INDEX_FIELD_IS_NULL(100009, "索引字段，不能为空"),

    OBJECT_ID_IS_NULL(100010, "批量修改field,objectId字段，不能为空");


    MeheExceptionEnum(int code, String msg) {
        this(code, null, msg);
    }

    MeheExceptionEnum(int code, HttpStatus httpStatus, String msg) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus == null ? HttpStatus.OK : httpStatus;
    }


    public int code;
    public String msg;
    public HttpStatus httpStatus;
}
