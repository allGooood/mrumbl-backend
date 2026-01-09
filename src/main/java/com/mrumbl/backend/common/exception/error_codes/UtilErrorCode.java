package com.mrumbl.backend.common.exception.error_codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UtilErrorCode implements ErrorCode{

    MAIL_SEND_FAILED(500, "EXT-001", "Failed to send mail.");

    private final int status;
    private final String code;
    private final String message;
}
