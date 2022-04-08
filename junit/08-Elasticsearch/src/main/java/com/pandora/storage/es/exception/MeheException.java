package com.pandora.storage.es.exception;

/**
 * 自定义异常
 *
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/2 16:44
 */
public class MeheException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;
    private int httpCode = 200;

    public MeheException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public MeheException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public MeheException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public MeheException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public MeheException(MeheExceptionEnum exceptionEumn) {
        super(exceptionEumn.msg);
        this.msg = exceptionEumn.msg;
        this.code = exceptionEumn.code;
        this.httpCode = exceptionEumn.httpStatus.value();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
