package com.mrumbl.backend.controller.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

// TODO - 레이어 결합도 분리를 위해 따로 작성을 다시 해야할지
@Builder
@Getter
public class OrderItemDto {
    @NotNull(message = "productId must not be null")
    @Positive(message = "productId must be positive")
    private Long productId;

    private String productName;

    @NotNull(message = "quantity must not be null")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    private String options;

    @NotNull(message = "unitAmount must not be null")
    @Positive(message = "unitAmount must be positive")
    private Integer unitAmount;

    @NotNull(message = "productAmount must not be null")
    @Positive(message = "productAmount must be positive")
    private Integer productAmount;

    private String imageUrl;
}
