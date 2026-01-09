package com.mrumbl.backend.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_NOT_FOUND(404, "CAR-001", "Cart not found."),
    CART_ITEM_NOT_FOUND(404, "CAR-002", "Product not found in cart."),
    CART_ITEM_ALREADY_EXISTS(409, "CAR-003", "Product already exists in cart."),
    CART_INSUFFICIENT_STOCK(409, "CAR-004", "Product cannot be added due to insufficient stock."),
    INVALID_CART_QUANTITY(400, "CAR-005", "Quantity must be at least 1.");

    private final int status;
    private final String code;
    private final String message;
}
