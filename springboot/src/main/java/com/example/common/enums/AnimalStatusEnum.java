package com.example.common.enums;

public enum AnimalStatusEnum {
    ADOPT_NO("Awaiting adoption"),
    ADOPT_YES("Under Adoption"),
    ADOPT_CANCEL("has been returned"),
    ;
    public String status;

    AnimalStatusEnum(String status) {
        this.status = status;
    }
}
