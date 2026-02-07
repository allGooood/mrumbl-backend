package com.mrumbl.backend.controller.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductDetailResponse {
    private Long productId;
    private String productName;
    private Integer unitAmount;
    private String description;
    private Integer stock;
    private String imageUrl;
    private BigDecimal discountRate;
    private String productType;
    private Integer requiredItemCount;
}
