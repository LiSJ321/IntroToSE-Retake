package com.example.common.enums;

public enum FosterStatusEnum {
    ADOPT_NO("Waiting"),
    ADOPTING("Under foster care"),
    ADOPTED("Finished"),
    ;
    public String status;

    FosterStatusEnum(String status) {
        this.status = status;
    }
}
