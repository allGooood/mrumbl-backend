package com.mrumbl.backend.service.product.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.ProductErrorCode;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.repository.product.ProductRepository;
import com.mrumbl.backend.repository.product.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    public Product checkAndReturnProduct(Long productId){
        return productRepository.findByIdAndInUse(productId, true)
                .orElseThrow(() -> {
                   log.warn("Product not found or inactive. productId={}", productId);
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });
    }

    public ProductStock checkAndReturnProductStock(Long storeId, Long productId){
        return productStockRepository.findByStoreIdAndProductIdWithFetchJoin(storeId, productId)
                .orElseThrow(() -> {
                    log.warn("Product not found in store. productId={}, storeId={}", productId, storeId);
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });
    }

    // checkAllIsInUseAndReturnProducts
    public List<Product> checkAndReturnProducts(List<Long> productIds) {
        List<Product> products = productRepository.findAllByIdInAndInUse(productIds, true);

        // 모든 productId가 존재하는지 검증
        if (products.size() != productIds.size()) {
            log.warn("Some products not found or inactive. productIds={}", productIds);
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }
}
