package com.mrumbl.backend.service.order.mapper;

import com.mrumbl.backend.controller.order.dto.GetOrderDetailResDto;
import com.mrumbl.backend.controller.order.dto.GetOrderResDto;
import com.mrumbl.backend.controller.order.dto.OrderItemDto;
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

    // TODO - OrderItem Mapper 분리 할지 말지
    public static OrderItemDto toOrderItemDto(OrderItem item) {
        return OrderItemDto.builder()
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductNameSnapshot())
                .quantity(item.getQuantity())
                .options(item.getOptions())
                .unitAmount(item.getUnitAmountSnapshot())
                .productAmount(item.getProductAmountSnapshot())
                .imageUrl(item.getImageUrlSnapshot())
                .build();
    }

    public static GetOrderDetailResDto toGetOrderDetailResDto(Order order){
        List<OrderItemDto> items = order.getOrderItems()
                .stream()
                .map(OrderMapper::toOrderItemDto)
                .toList();

        return GetOrderDetailResDto.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderedAt(order.getOrderedAt())
                .orderState(order.getOrderState())
                .items(items)
                .paymentAmount(order.getPaymentAmount())
                .productAmount(order.getProductAmount())
                .taxAmount(order.getTaxAmount())
                .customerRequest(order.getCustomerRequest())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }

}
