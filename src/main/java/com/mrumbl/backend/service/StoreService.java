package com.mrumbl.backend.service;

import com.mrumbl.backend.controller.store.dto.StoreListResponse;
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
        log.info("[StoreService] Search request received. keyword={}", keyword);

        List<Store> results = storeRepository.searchByKeyword(keyword);
        log.info("[StoreService] Found {} stores for keyword={}", results.size(), keyword);

        List<StoreListResponse.StoreResponse> stores = results.stream()
                .map(store -> StoreListResponse.StoreResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .storeAddress(StoreListResponse.AddressResponse.builder()
                                .address(store.getAddress())
                                .addressDetail(store.getAddressDetail())
                                .postcode(store.getPostcode())
                                .build())
                        .isOpen(isNowOpen(store.getOpenTime(), store.getCloseTime()))
                        .build()
                )
                .toList();

        return StoreListResponse.builder()
                .stores(stores)
                .build();
    }

    private boolean isNowOpen(LocalTime openTime, LocalTime closeTime){
        LocalTime now = LocalTime.now();
        return now.isBefore(closeTime) && now.isAfter(openTime);
    }
}
