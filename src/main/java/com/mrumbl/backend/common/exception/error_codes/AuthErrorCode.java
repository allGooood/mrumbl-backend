package com.mrumbl.backend.common.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_EMAIL_FORMAT(400, "AUT-001", "Invalid email format."),
    AUTH_INFO_NOT_FOUND(404, "AUT-002", "Authentication information not found."),
    VERIFICATION_CODE_MISMATCH(400, "AUT-003", "Verification code does not match."),
    VERIFICATION_CODE_EXPIRED(400, "AUT-004", "Verification code has expired."),
    VERIFICATION_REQUEST_LIMIT_EXCEEDED(429, "AUT-005", "Verification request limit exceeded."),
    INVALID_CREDENTIALS(401, "AUT-006", "Email or password is incorrect."),
    INVALID_AUTH_TOKEN(401, "AUT-007", "Invalid authentication token."),
    EXPIRED_AUTH_TOKEN(401, "AUT-008", "Authentication token has expired."),
    LOGGED_OUT_TOKEN(401, "AUT-009", "Token has been logged out."),
    INVALID_REFRESH_TOKEN(401, "AUT-010", "Invalid refresh token."),
    UNSUPPORTED_TOKEN(401, "AUT-011", "Unsupported token format."),
    EMPTY_CLAIMS(401, "AUT-012", "Token Claims string is empty.");

    private final int status;
    private final String code;
    private final String message;
}
