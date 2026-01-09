package com.mrumbl.backend.common.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {

    STORE_NOT_FOUND(404, "STO-001", "Store not found."),
    INVALID_LOCATION_COORDINATES(400, "STO-002", "Invalid location coordinates."),
    NO_STORE_IN_RANGE(404, "STO-003", "No store found within the specified range."),
    STORE_ACCESS_RESTRICTED(403, "STO-004", "Access to this store is restricted.");

    private final int status;
    private final String code;
    private final String message;
}
