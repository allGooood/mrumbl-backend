package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CookieOptionDetailDto {
    private Long cookieId;
    private String cookieName;
    private Integer quantity;
    private Boolean isSoldOut;
}
