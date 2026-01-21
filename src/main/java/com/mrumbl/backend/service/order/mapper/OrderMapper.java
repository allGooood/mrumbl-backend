package com.mrumbl.backend.service.order.mapper;

import com.mrumbl.backend.controller.order.dto.GetOrderDetailResDto;
import com.mrumbl.backend.controller.order.dto.GetOrderResDto;
import com.mrumbl.backend.controller.order.dto.OrderItemDto;
import com.mrumbl.backend.domain.Order;
import com.mrumbl.backend.domain.OrderItem;

import java.util.List;

/**
 * Order Entity <-> DTO
 */
public class OrderMapper {

    public static GetOrderResDto toGetOrderResDto(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItem firstItem = orderItems.get(0);

        return GetOrderResDto.builder()
                .paymentAmount(order.getPaymentAmount())
                .imageUrl(firstItem.getImageUrlSnapshot())
                .itemCount(orderItems.size())
                .orderState(order.getOrderState())
                .orderId(order.getId())
                .orderedAt(order.getOrderedAt())
                .orderNo(order.getOrderNo())
                .build();
    }

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
