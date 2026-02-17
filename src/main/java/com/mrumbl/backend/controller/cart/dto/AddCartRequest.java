package com.mrumbl.backend.controller.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class AddCartRequest {
    @NotNull(message = "productId must not be null")
    @Positive(message = "productId must be positive")
    private Long productId;

    @NotNull(message = "storeId must not be null")
    @Positive(message = "storeId must be positive")
    private Long storeId;

    @NotNull(message = "quantity must not be null")
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @NotNull
    private String productType;

    @Valid
    private List<CookieOptionRequest> options;
}
