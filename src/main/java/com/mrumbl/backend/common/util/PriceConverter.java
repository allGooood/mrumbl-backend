package com.mrumbl.backend.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceConverter {

    private static final int CENTS_PER_DOLLAR = 100;
    private static final int SCALE = 2; // 소수점 2자리

    /**
     * 센트 -> 달러
     * 
     * @param cents 센트 단위 가격 (null 허용)
     * @return 달러 단위 가격 (BigDecimal), null이면 null 반환
     */
    public static BigDecimal centsToDollars(Integer cents) {
        if (cents == null) {
            return null;
        }
        return BigDecimal.valueOf(cents)
                .divide(BigDecimal.valueOf(CENTS_PER_DOLLAR), SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 달러 -> 센트
     * 
     * @param dollars 달러 단위 가격 (null 허용)
     * @return 센트 단위 가격 (Integer), null이면 null 반환
     */
    public static Integer dollarsToCents(BigDecimal dollars) {
        if (dollars == null) {
            return null;
        }
        return dollars.multiply(BigDecimal.valueOf(CENTS_PER_DOLLAR))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
