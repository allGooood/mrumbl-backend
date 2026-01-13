package com.mrumbl.backend.service;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.StoreErrorCode;
import com.mrumbl.backend.controller.store.dto.*;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public StoreListResponse searchStores(String keyword){
        log.info("[StoreService] searchStores request received. keyword={}", keyword);

        List<Store> results = storeRepository.searchByKeyword(keyword);
        log.info("[StoreService] Found {} stores for keyword={}", results.size(), keyword);

        List<StoreSummaryDto> stores = results.stream()
                .map(store -> StoreSummaryDto.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .storeAddress(StoreAddressDto.builder()
                                .address(store.getAddress())
                                .addressDetail(store.getAddressDetail())
                                .postcode(store.getPostcode())
                                .build())
                        .isOpenNow(isOpenNow(store.getOpenTime(), store.getCloseTime()))
                        .build()
                )
                .toList();

        return StoreListResponse.builder()
                .stores(stores)
                .build();
    }

    private boolean isOpenNow(LocalTime openTime, LocalTime closeTime){
        LocalTime now = LocalTime.now();
        return now.isBefore(closeTime) && now.isAfter(openTime);
    }

    @Transactional(readOnly = true)
    public GetStoreResDto getStore(Long storeId){
        log.info("[StoreService] getStore request received. storeId={}", storeId);

        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("[StoreService] Store not found. storeId={}", storeId);
                    return new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
                });

        boolean isOpenNow = isOpenNow(storeEntity.getOpenTime(), storeEntity.getCloseTime());

        return GetStoreResDto.builder()
                .storeId(storeEntity.getId())
                .storeName(storeEntity.getStoreName())
                .storeAddress(StoreAddressDto.builder()
                        .address(storeEntity.getAddress())
                        .addressDetail(storeEntity.getAddressDetail())
                        .postcode(storeEntity.getPostcode())
                        .build())
                .isOpenNow(isOpenNow)
                .storeBusinessHour(StoreBusinessHourDto.builder()
                        .open(storeEntity.getOpenTime())
                        .close(storeEntity.getCloseTime())
                        .build())
                .build();
    }
}
