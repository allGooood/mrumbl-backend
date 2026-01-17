package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartResDto {
    private List<String> cartIds;
}
