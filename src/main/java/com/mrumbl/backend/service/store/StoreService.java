package com.mrumbl.backend.service.store;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.StoreErrorCode;
import com.mrumbl.backend.controller.store.dto.GetStoreResDto;
import com.mrumbl.backend.controller.store.dto.StoreListResponse;
import com.mrumbl.backend.controller.store.dto.StoreSummaryDto;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.store.StoreRepository;
import com.mrumbl.backend.service.store.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                .map(StoreMapper::toStoreSummaryDto)
                .toList();

        return StoreListResponse.builder()
                .stores(stores)
                .build();
    }

    @Transactional(readOnly = true)
    public GetStoreResDto getStore(Long storeId){
        log.info("[StoreService] getStore request received. storeId={}", storeId);

        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("[StoreService] Store not found. storeId={}", storeId);
                    return new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
                });

        return StoreMapper.toGetStoreResDto(storeEntity);
    }

    @Transactional(readOnly = true)
    public StoreListResponse getStoreNearby(BigDecimal x, BigDecimal y, Integer r){

        List<Store> results = storeRepository.findNearbyStores(x, y, r);

        List<StoreSummaryDto> stores = results.stream()
                .map(store -> StoreMapper.toStoreSummaryDto(store, true))
                .toList();

        return StoreListResponse.builder()
                .stores(stores)
                .build();
    }
}
