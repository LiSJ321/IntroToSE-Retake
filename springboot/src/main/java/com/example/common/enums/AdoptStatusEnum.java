package com.example.common.enums;

public enum AdoptStatusEnum {
    ADOPT_YES("under adoption"),
    ADOPT_NO("has been returned"),
    ;
    public String status;

    AdoptStatusEnum(String status) {
        this.status = status;
    }
}
