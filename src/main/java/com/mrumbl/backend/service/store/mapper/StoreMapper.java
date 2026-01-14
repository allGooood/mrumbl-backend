package com.mrumbl.backend.service.store.mapper;

import com.mrumbl.backend.controller.store.dto.*;
import com.mrumbl.backend.domain.Store;

import java.time.LocalTime;

/**
 * Store 엔티티를 DTO로 변환하는 Mapper 클래스
 */
public class StoreMapper {

    /**
     * StoreSummaryDto 생성 (좌표 O)
     * 
     * @param store 변환할 Store 엔티티
     * @param includeCoordinates 좌표 포함 여부
     * @return StoreSummaryDto
     */
    public static StoreSummaryDto toStoreSummaryDto(Store store, boolean includeCoordinates) {
        StoreSummaryDto.StoreSummaryDtoBuilder builder = StoreSummaryDto.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .storeAddress(toStoreAddressDto(store))
                .isOpenNow(isOpenNow(store.getOpenTime(), store.getCloseTime()));

        if (includeCoordinates && store.getXCoordinate() != null && store.getYCoordinate() != null) {
            builder.coordinates(CoordinateDto.builder()
                    .longitude(store.getXCoordinate())
                    .latitude(store.getYCoordinate())
                    .build());
        }
        return builder.build();
    }

    /**
     * StoreSummaryDto 생성 (좌표 X)
     */
    public static StoreSummaryDto toStoreSummaryDto(Store store) {
        return toStoreSummaryDto(store, false);
    }

    /**
     * GetStoreResDto 생성
     */
    public static GetStoreResDto toGetStoreResDto(Store store) {
        return GetStoreResDto.builder()
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

    /**
     * StoreAddressDto 생성
     */
    private static StoreAddressDto toStoreAddressDto(Store store) {
        return StoreAddressDto.builder()
                .address(store.getAddress())
                .addressDetail(store.getAddressDetail())
                .postcode(store.getPostcode())
                .build();
    }

    /**
     * 현재 시간 기준 영업 여부 확인
     */
    private static boolean isOpenNow(LocalTime openTime, LocalTime closeTime) {
        LocalTime now = LocalTime.now();
        return now.isBefore(closeTime) && now.isAfter(openTime);
    }
}
