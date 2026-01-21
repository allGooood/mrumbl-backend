package com.mrumbl.backend.common.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(404, "PRO-001", "Product not found."),
    PRODUCT_NOT_AVAILABLE(409, "PRO-002", "Product is no longer available for sale."),
    INVALID_PRODUCT_TYPE(400, "PRO-003", "Invalid product type."),
    INSUFFICIENT_STOCK(409, "PRO-004", "Insufficient stock."),
    INVALID_PRODUCT_CATEGORY(400, "PRO-006", "Invalid product category."),
    PURCHASE_QUANTITY_EXCEEDED(400, "PRO-005", "Purchase quantity exceeds the allowed limit.");

    private final int status;
    private final String code;
    private final String message;
}
