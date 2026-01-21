package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.util.EnumConverter;

public enum PaymentMethod {
    CARD;

    public static PaymentMethod from(String paymentMethodStr) {
        return EnumConverter.from(paymentMethodStr, PaymentMethod.class, "PaymentMethod");
    }
}
