package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CoordinateDto {
    private BigDecimal longitude;
    private BigDecimal latitude;
}
