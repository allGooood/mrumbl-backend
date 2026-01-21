package com.mrumbl.backend.service.store.mapper;

import com.mrumbl.backend.controller.store.dto.*;
import com.mrumbl.backend.domain.Store;

import java.time.LocalTime;

/**
 * Store Entity <-> DTO
 */
public class StoreMapper {

    public static StoreInformationDto toStoreInformationDto(Store store) {
        StoreInformationDto.StoreInformationDtoBuilder builder = StoreInformationDto.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .storeAddress(toStoreAddressDto(store))
                .isOpenNow(isOpenNow(store.getOpenTime(), store.getCloseTime()));

        if (store.getXCoordinate() != null && store.getYCoordinate() != null) {
            builder.coordinates(CoordinateDto.builder()
                    .longitude(store.getXCoordinate())
                    .latitude(store.getYCoordinate())
                    .build());
        }
        return builder.build();
    }

    public static StoreDetailResponse toStoreDetailResponse(Store store) {
        return StoreDetailResponse.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .storeAddress(toStoreAddressDto(store))
                .isOpenNow(isOpenNow(store.getOpenTime(), store.getCloseTime()))
                .storeBusinessHour(StoreBusinessHourDto.builder()
                        .open(store.getOpenTime())
                        .close(store.getCloseTime())
                        .build())
                .build();
    }

    private static StoreAddressDto toStoreAddressDto(Store store) {
        return StoreAddressDto.builder()
                .address(store.getAddress())
                .addressDetail(store.getAddressDetail())
                .postcode(store.getPostcode())
                .build();
    }

    /**
     * 현재 시간 기준 영업 여부 확인
     * 자정을 넘어가는 영업 시간도 처리 (예: 22:00 ~ 02:00)
     */
    private static boolean isOpenNow(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            return false;
        }
        
        LocalTime now = LocalTime.now();
        
        // 자정을 넘어가는 경우 (예: 22:00 ~ 02:00)
        if (closeTime.isBefore(openTime)) {
            return now.isAfter(openTime) || now.isBefore(closeTime);
        }
        
        // 일반적인 경우 (예: 09:00 ~ 22:00)
        return now.isAfter(openTime) && now.isBefore(closeTime);
    }
}
