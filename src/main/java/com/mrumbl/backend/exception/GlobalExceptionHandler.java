package com.mrumbl.backend.exception;

import com.mrumbl.backend.exception.error_codes.CommonErrorCode;
import com.mrumbl.backend.exception.error_codes.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Unspecified Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        return ResponseEntity
                .status(500)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.getCode())
                        .message(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode(CommonErrorCode.INVALID_REQUEST.getCode())
                        .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                        .build());
    }
}
