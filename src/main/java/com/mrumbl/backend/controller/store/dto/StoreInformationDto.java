package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreInformationDto {
    private Long storeId;
    private String storeName;
    private StoreAddressDto storeAddress;
    private boolean isOpenNow;
    private CoordinateDto coordinates;
}
