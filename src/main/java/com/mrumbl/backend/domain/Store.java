package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    private BigDecimal xCoordinate;
    private BigDecimal yCoordinate;

    private String address;
    private String addressDetail;
    private String postcode;

    private LocalTime openTime;
    private LocalTime closeTime;

    @Column(insertable = false)
    private boolean isActive;

    @Column(insertable = false) // 관리자 페이지가 만들어지는 경우에는 insertable 사용하지 X
    private boolean isDefaultStore;
}
