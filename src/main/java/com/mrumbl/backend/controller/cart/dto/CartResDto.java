package com.mrumbl.backend.controller.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class CartResDto {
    private Set<String> cartIds;
}
