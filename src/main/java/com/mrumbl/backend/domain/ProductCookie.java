package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.loadtime.definition.Definition;

@Table(name = "product_cookie")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCookie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cookieName;
    private String imageUrl;
    private Integer cookieCalorie;
    private Integer additionalPrice;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean inUse;

    @Column(columnDefinition = "TEXT")
    private String description;
}
