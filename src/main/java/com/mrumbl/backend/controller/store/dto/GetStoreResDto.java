package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class GetStoreResDto {
    private Long storeId;
    private String storeName;
    private StoreAddressDto storeAddress;
    private boolean isOpenNow;
    private StoreBusinessHourDto storeBusinessHour;
}
