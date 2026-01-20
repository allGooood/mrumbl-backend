package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.member m " +
            "JOIN FETCH o.store s " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "WHERE m.email = :email " +
            "ORDER BY o.orderedAt DESC, oi.id")
    List<Order> findOrdersAndItemsByEmail(@Param("email") String email);
}
