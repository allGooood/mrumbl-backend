package com.mrumbl.backend.controller.order.dto;

import com.mrumbl.backend.common.enumeration.OrderState;
import com.mrumbl.backend.common.enumeration.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class GetOrderDetailResDto {
    private Long orderId;
    private String orderNo;
    private LocalDateTime orderedAt;
    private OrderState orderState;
    private List<OrderItemDto> items;
    private Integer paymentAmount;
    private Integer productAmount;
    private Integer taxAmount;
    private String customerRequest;
    private PaymentMethod paymentMethod;
}
