package com.mrumbl.backend.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_ITEMS_NOT_FOUND(400, "ORD-001", "No items found for order."),
    ORDER_INSUFFICIENT_STOCK(409, "ORD-002", "Insufficient stock to place the order."),
    ORDER_ACCESS_DENIED(403, "ORD-003", "You are not authorized to access this order."),
    ORDER_NOT_FOUND(404, "ORD-004", "Order not found."),
    ORDER_ALREADY_PROCESSED(409, "ORD-005", "Order has already been processed."),
    INVALID_ORDER_STATE(400, "ORD-006", "Order state cannot be changed."),
    ORDER_CANCELLATION_EXPIRED(403, "ORD-007", "Order cancellation period has expired."),
    PAYMENT_FAILED(402, "ORD-008", "Payment failed.");

    private final int status;
    private final String code;
    private final String message;
}
