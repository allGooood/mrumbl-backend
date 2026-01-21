package com.mrumbl.backend.repository.product;

import com.mrumbl.backend.domain.ProductStock;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepositoryCustom {
    List<ProductStock> findByStoreIdWithFetchJoin(Long storeId);
    Optional<ProductStock> findByStoreIdAndProductId(Long storeId, Long productId);
}
