package com.mrumbl.backend.controller.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AddOrderReqDto {
    @NotEmpty(message = "products must not be empty")
    @Valid
    private List<OrderProductDto> products;

    private String customerRequest;

    @NotNull(message = "storeId must not be null")
    @Positive(message = "storeId must be positive")
    private Long storeId;

//    private Long memberId;

    @NotNull(message = "paymentMethod must not be null")
    private String paymentMethod;

    @NotNull(message = "productAmount must not be null")
    @Positive(message = "productAmount must be positive")
    private Integer productAmount;

    @NotNull(message = "taxAmount must not be null")
    @Positive(message = "taxAmount must be positive")
    private Integer taxAmount;

    @NotNull(message = "paymentAmount must not be null")
    @Positive(message = "paymentAmount must be positive")
    private Integer paymentAmount;

    private Set<String> cartIds;

    @Data
    public static class OrderProductDto {
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
}
