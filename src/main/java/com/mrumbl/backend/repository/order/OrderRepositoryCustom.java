package com.mrumbl.backend.repository.order;

import com.mrumbl.backend.domain.Order;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {
    List<Order> findOrdersAndItemsByEmail(String email);
    Optional<Order> findOrderAndItemsByOrderIdAndEmail(Long orderId, String email);
}
