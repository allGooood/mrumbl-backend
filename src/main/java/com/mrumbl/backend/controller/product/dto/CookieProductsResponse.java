package com.mrumbl.backend.controller.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CookieProductsResponse {
    private Long cookieId;
    private String cookieName;
    private String imageUrl;
    private Integer cookieCalorie;
    private BigDecimal additionalPrice; // 달러 단위 (DB는 센트로 저장)
    private String description;
}
