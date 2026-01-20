package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PaymentMethod {
    CARD;

    public static PaymentMethod from(String paymentMethodStr) {
        try {
            return PaymentMethod.valueOf(paymentMethodStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid payment method. paymentMethod={}", paymentMethodStr);
            throw new BusinessException(OrderErrorCode.PAYMENT_FAILED);
        }
    }
}
