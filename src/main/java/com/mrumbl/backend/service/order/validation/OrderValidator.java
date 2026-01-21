package com.mrumbl.backend.service.order.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.OrderErrorCode;
import com.mrumbl.backend.controller.order.dto.OrderItemDto;
import com.mrumbl.backend.domain.Order;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderRepository orderRepository;

    public Order checkAndReturnExistingOrder(String email, Long orderId){
        return orderRepository.findOrderAndItemsByOrderIdAndEmail(orderId, email)
                .orElseThrow(() -> {
                    log.warn("Order not found. email={}, orderId={}", email, orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });
    }

}
