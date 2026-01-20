package com.mrumbl.backend.service.order.mapper;

import com.mrumbl.backend.controller.order.dto.GetOrderResDto;
import com.mrumbl.backend.domain.Order;
import com.mrumbl.backend.domain.OrderItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Order Entity <-> DTO로 변환하는 Mapper 클래스
 */
@Slf4j
public class OrderMapper {

    public static GetOrderResDto toGetOrderResDto(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();

        if (orderItems == null || orderItems.isEmpty()) {
            log.warn("Order has no items. orderId={}", order.getId());
            return GetOrderResDto.builder()
                    .paymentAmount(order.getPaymentAmount())
                    .imageUrl(null)
                    .itemCount(0)
                    .orderState(order.getOrderState())
                    .orderId(order.getId())
                    .orderedAt(order.getOrderedAt())
                    .build();
        }

        OrderItem firstItem = orderItems.get(0);

        return GetOrderResDto.builder()
                .paymentAmount(order.getPaymentAmount())
                .imageUrl(firstItem.getImageUrlSnapshot())
                .itemCount(orderItems.size())
                .orderState(order.getOrderState())
                .orderId(order.getId())
                .orderedAt(order.getOrderedAt())
                .build();
    }
}
