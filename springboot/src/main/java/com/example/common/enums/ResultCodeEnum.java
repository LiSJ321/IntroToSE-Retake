package com.example.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "success"),

    PARAM_ERROR("400", "PARAM_ERROR"),
    TOKEN_INVALID_ERROR("401", "TOKEN_INVALID_ERROR"),
    TOKEN_CHECK_ERROR("401", "TOKEN_CHECK_ERROR"),
    PARAM_LOST_ERROR("4001", "PARAM_LOST_ERROR"),

    SYSTEM_ERROR("500", "SYSTEM_ERROR"),
    USER_EXIST_ERROR("5001", "USER_EXIST_ERROR"),
    USER_NOT_LOGIN("5002", "USER_NOT_LOGIN"),
    USER_ACCOUNT_ERROR("5003", "USER_ACCOUNT_ERROR"),
    USER_NOT_EXIST_ERROR("5004", "USER_NOT_EXIST_ERROR"),
    PARAM_PASSWORD_ERROR("5005", " PARAM_PASSWORD_ERROR"),
    ACCOUNT_LOW_ERROR("5006","Balance is not enough,please top up")
    ;

    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
