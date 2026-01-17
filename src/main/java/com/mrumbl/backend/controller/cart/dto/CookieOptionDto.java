package com.mrumbl.backend.controller.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CookieOptionDto {
    @NotNull(message = "cookieId must not be null")
    @Positive(message = "cookieId must be positive")
    private Long cookieId;

    @NotNull(message = "quantity must not be null")
    @Positive(message = "quantity must be positive")
    private Integer quantity;
}
