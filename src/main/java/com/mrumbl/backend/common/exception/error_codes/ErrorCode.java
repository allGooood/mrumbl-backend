package com.mrumbl.backend.common.exception.error_codes;

public interface ErrorCode {
    int getStatus(); // HTTP Status Code
    String getCode(); // internal Error Code (Ex. ORD-002)
    String getMessage();
}
