package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class StoreBusinessHourDto {
    private LocalTime open;
    private LocalTime close;
}
