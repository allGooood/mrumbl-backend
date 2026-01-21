package com.mrumbl.backend.service.store;

import com.mrumbl.backend.controller.store.dto.StoreDetailResponse;
import com.mrumbl.backend.controller.store.dto.StoreListResponse;
import com.mrumbl.backend.controller.store.dto.StoreInformationDto;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.store.StoreRepository;
import com.mrumbl.backend.service.store.mapper.StoreMapper;
import com.mrumbl.backend.service.store.validation.StoreValidator;
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

    private final StoreValidator storeValidator;

    @Transactional(readOnly = true)
    public StoreListResponse searchStores(String keyword){
        String trimmedKeyword = keyword.trim();
        List<Store> storesFound = storeRepository.searchByKeyword(trimmedKeyword);
        log.info("Found {} stores for keyword={}", storesFound.size(), trimmedKeyword);

        List<StoreInformationDto> storeDtos = storesFound.stream()
                .map(StoreMapper::toStoreInformationDto)
                .toList();

        return StoreListResponse.builder()
                .stores(storeDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public StoreDetailResponse getStore(Long storeId){
        Store storeFound = storeValidator.checkAndReturnStore(storeId);
        log.info("Store found. storeId={}, storeName={}", storeId, storeFound.getStoreName());

        return StoreMapper.toStoreDetailResponse(storeFound);
    }

    @Transactional(readOnly = true)
    public StoreListResponse getNearbyStores(BigDecimal longitude, BigDecimal latitude, Integer radius){
        List<Store> storesFound = storeRepository.findNearbyStores(longitude, latitude, radius);
        log.info("Found {} stores within {}m radius", storesFound.size(), radius);

        List<StoreInformationDto> storeDtos = storesFound.stream()
                .map(StoreMapper::toStoreInformationDto)
                .toList();

        return StoreListResponse.builder()
                .stores(storeDtos)
                .build();
    }
}
