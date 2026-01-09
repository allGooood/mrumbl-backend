package com.mrumbl.backend.common.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS(409, "ACC-001", "Email already exists."),
    PASSWORD_POLICY_VIOLATION(400, "ACC-002", "Password does not meet security requirements."),
    MEMBER_NOT_FOUND(404, "ACC-003", "Member not found."),
    ACCOUNT_DEACTIVATED(403, "ACC-004", "Account is deactivated."),
    ACCOUNT_WITHDRAWN(403, "ACC-005", "Account has been withdrawn."),
    INVALID_ADDRESS(400, "ACC-006", "Invalid address information."),
    ADDRESS_NOT_CHANGED(409, "ACC-007", "Address cannot be updated with the same value.");

    private final int status;
    private final String code;
    private final String message;
}
