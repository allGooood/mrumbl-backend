package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import com.mrumbl.backend.common.enumeration.OrderState;
import com.mrumbl.backend.common.enumeration.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    private String orderNo;

    @OneToOne
    private Store store;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Integer paymentAmount;
    private Integer productAmount;
    private Integer taxAmount;
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

}
