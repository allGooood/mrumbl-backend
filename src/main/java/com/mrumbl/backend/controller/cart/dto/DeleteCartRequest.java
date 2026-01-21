package com.mrumbl.backend.controller.cart.dto;

import lombok.Data;

import java.util.Set;

@Data
public class DeleteCartRequest {
    private Set<String> cartIds;
}
