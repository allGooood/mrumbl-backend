package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import com.mrumbl.backend.common.enumeration.ProductCategory;
import com.mrumbl.backend.common.enumeration.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private String imageUrl;
    private Integer unitAmount;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private Integer requiredItemCount;

    private BigDecimal discountRate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean inUse;
}
