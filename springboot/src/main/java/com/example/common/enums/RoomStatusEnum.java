package com.example.common.enums;

public enum RoomStatusEnum {
    ROOM_OK("Free"),
    ROOM_NO("Occupied"),
    ;
    public String status;

    RoomStatusEnum(String status) {
        this.status = status;
    }
}
