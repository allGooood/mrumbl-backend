package com.mrumbl.backend.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final boolean success;
    private final String errorCode;
    private final String message;
}
