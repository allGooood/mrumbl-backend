package com.mrumbl.backend.controller.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@ToString
public class StoreProductsResponse {
    private Long storeId;
    private String category;
    private Integer displayOrder;
    private List<StoreProductDto> products;

    @Getter
    @Builder
    @ToString
    public static class StoreProductDto {
        private Long productId;
        private String productName;
        private Integer unitAmount;
        private BigDecimal discountRate;
        private Boolean isSoldOut;
        private String productType;
        private String imageUrl;
        private Integer requiredItemCount;
    }
}
