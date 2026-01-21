package com.mrumbl.backend.service.product.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.ProductErrorCode;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.repository.product.ProductRepository;
import com.mrumbl.backend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public Product checkAndReturnProduct(Long productId){
        return productRepository.findByIdAndInUse(productId, true)
                .orElseThrow(() -> {
                   log.warn("Product not found or inactive. productId={}", productId);
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });
    }
}
