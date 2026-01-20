package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
