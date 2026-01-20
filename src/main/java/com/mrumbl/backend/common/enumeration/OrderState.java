package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum OrderState {
    CREATED,
    PAID,
    PREPARING,
    READY,
    PICKED_UP,
    CANCELED;

    public static OrderState from(String orderStateStr){
        try{
            return OrderState.valueOf(orderStateStr);
        }catch (Exception e){
            log.warn("Invalid order state. orderState={}", orderStateStr);
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATE);
        }
    }
}
