package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CookieOptionDetailResponse {
    private Long cookieId;
    private String cookieName;
    private Integer quantity;
    private Boolean isSoldOut;
}
