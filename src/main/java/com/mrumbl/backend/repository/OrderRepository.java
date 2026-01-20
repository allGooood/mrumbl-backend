package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
