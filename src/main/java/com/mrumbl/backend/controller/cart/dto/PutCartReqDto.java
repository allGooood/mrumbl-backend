package com.mrumbl.backend.controller.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class PutCartReqDto {
    private String cartId;
    private Integer quantity;
    List<CookieOptionDto> options;
}
