package com.mrumbl.backend.service.store.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.StoreErrorCode;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreValidator {
    private final StoreRepository storeRepository;

    public Store checkAndReturnStore(Long storeId){
        return storeRepository.findByIdAndIsActiveIsTrue(storeId)
                .orElseThrow(() -> {
                    storeNotFoundOrInactive(storeId);
                    return new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
                });
    }

    public void checkExistingStore(Long storeId){
        if (!storeRepository.existsByIdAndIsActiveIsTrue(storeId)) {
            storeNotFoundOrInactive(storeId);
            throw new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
        }
    }

    private void storeNotFoundOrInactive(Long storeId) {
        log.warn("Store not found or Inactive. storeId={}", storeId);
    }
}
