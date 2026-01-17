package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class GetCartResDto {
    private String cartId;

    private Long productId;
    private String productName;
    private BigDecimal unitAmount;
    private BigDecimal productAmount;
    private String imageUrl;
    private Boolean isSoldOut; //TODO - 응답 실제 필드명 확인
    private Integer requiredItemCount;

    private Integer quantity;
    private List<CookieOptionDetailDto> options;
}
