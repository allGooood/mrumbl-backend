package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "store_product_stock")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer stockQuantity;

    @Formula("(CASE WHEN stock_quantity = 0 THEN true ELSE false END)")
    private Boolean isSoldOut;
}
