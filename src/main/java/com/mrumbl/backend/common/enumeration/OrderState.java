package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.util.EnumConverter;

public enum OrderState {
    CREATED,
    PAID,
    PREPARING,
    READY,
    PICKED_UP,
    CANCELED;

    public static OrderState from(String orderStateStr) {
        return EnumConverter.from(orderStateStr, OrderState.class, "OrderState");
    }
}
