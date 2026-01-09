package com.mrumbl.backend.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_REQUEST(400, "COM-001", "Invalid request format."),
    MISSING_PARAMETER(400, "COM-002", "Required parameter is missing."),
    INVALID_PARAMETER_TYPE(400, "COM-003", "Invalid parameter type."),
    UNAUTHORIZED(401, "COM-004", "Authentication is required."),
    FORBIDDEN(403, "COM-005", "Access is denied."),
    RESOURCE_NOT_FOUND(404, "COM-006", "Requested resource not found."),
    INVALID_STATE(409, "COM-007", "Request cannot be processed in the current state."),
    INTERNAL_SERVER_ERROR(500, "COM-008", "Internal server error occurred."),
    SERVICE_UNAVAILABLE(503, "COM-009", "Service is temporarily unavailable.");

    private final int status;
    private final String code;
    private final String message;
}
