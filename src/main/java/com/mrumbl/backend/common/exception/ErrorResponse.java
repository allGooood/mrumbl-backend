package com.mrumbl.backend.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final String errorCode;
    private final String message;
}
