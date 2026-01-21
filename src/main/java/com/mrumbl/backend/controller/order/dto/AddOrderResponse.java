package com.mrumbl.backend.controller.order.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddOrderResponse {
    private String orderNo;
}
