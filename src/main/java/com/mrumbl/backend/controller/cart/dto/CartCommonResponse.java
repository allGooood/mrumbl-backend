package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CartCommonResponse {
    private Set<String> cartIds;
}
