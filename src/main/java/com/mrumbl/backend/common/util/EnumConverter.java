package com.mrumbl.backend.common.util;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumConverter {

    public static <T extends Enum<T>> T from(String valueStr, Class<T> classObj, String className) {
        if (valueStr == null || valueStr.isBlank()) {
            log.warn("Invalid {} value. value is null or blank", className);
            throw new BusinessException(CommonErrorCode.INVALID_ENUM_VALUE);
        }

        try {
            return Enum.valueOf(classObj, valueStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid {} value. value={}, validValues={}", 
                    className, valueStr, java.util.Arrays.toString(classObj.getEnumConstants()));
            throw new BusinessException(CommonErrorCode.INVALID_ENUM_VALUE);
        }
    }
}
