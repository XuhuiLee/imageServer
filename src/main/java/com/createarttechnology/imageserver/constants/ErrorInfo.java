package com.createarttechnology.imageserver.constants;

/**
 * Created by lixuhui on 2018/9/14.
 */
public enum ErrorInfo {
    SUCCESS(0, "success"),
    ERROR(-1, "error"),
    DB_ERROR(-2, "db error"),
    INVALID_PARAMS(-3, "invalid params"),
    NO_AUTH(-4, "no auth"),
    ;



    private int code;
    private String msg;

    ErrorInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
