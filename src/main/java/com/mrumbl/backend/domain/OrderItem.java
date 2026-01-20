package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private String productNameSnapshot;
    private Integer productAmountSnapshot;

    @Column(columnDefinition = "TEXT")
    private String options;

    private Integer quantity;
    private Integer unitAmountSnapshot;
    private String imageUrlSnapshot;
}
