package com.mrumbl.backend.controller.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCartReqDto {
    private List<String> cartIds;
}
