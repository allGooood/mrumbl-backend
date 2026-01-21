package com.mrumbl.backend.controller.order.dto;

import com.mrumbl.backend.common.enumeration.OrderState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetOrderResDto {
    private Integer paymentAmount;
    private String imageUrl;
    private Integer itemCount;
    private OrderState orderState;
    private Long orderId;
    private LocalDateTime orderedAt;
    private String orderNo;
}
