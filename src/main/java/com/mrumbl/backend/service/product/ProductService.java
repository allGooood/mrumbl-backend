package com.mrumbl.backend.service.product;

import com.mrumbl.backend.common.enumeration.ProductCategory;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.ProductErrorCode;
import com.mrumbl.backend.common.exception.error_codes.StoreErrorCode;
import com.mrumbl.backend.controller.product.dto.GetProductDetailResDto;
import com.mrumbl.backend.controller.product.dto.GetStoreProductsResDto;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.repository.ProductStockRepository;
import com.mrumbl.backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductStockRepository productStockRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<GetStoreProductsResDto> getProducts(Long storeId){
        log.info("[ProductService] getProducts request received. storeId={}", storeId);

        if (!storeRepository.existsById(storeId)) {
            log.warn("[ProductService] Store not found. storeId={}", storeId);
            throw new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
        }

        List<ProductStock> stocks = productStockRepository.findByStoreIdWithStoreAndProduct(storeId);
        log.info("[ProductService] Found {} products for storeId={}", stocks.size(), storeId);

        Map<ProductCategory, List<GetStoreProductsResDto.StoreProductDto>> map = stocks
                .stream()
                .collect(Collectors.groupingBy(
                        stock -> stock.getProduct().getProductCategory(),
                        Collectors.mapping(
                                stock -> {
                                    Product product = stock.getProduct();
                                    return GetStoreProductsResDto.StoreProductDto.builder()
                                            .productId(product.getId())
                                            .productName(product.getProductName())
                                            .unitAmount(product.getUnitAmount())
                                            .discountRate(product.getDiscountRate())
                                            .isSoldOut(stock.getIsSoldOut())
                                            .productType(String.valueOf(product.getProductType()))
                                            .imageUrl(product.getImageUrl())
                                            .requiredItemCount(product.getRequiredItemCount())
                                            .build();
                                },
                                Collectors.toList()
                        )
                ));

        return map.entrySet()
                .stream()
                .map(entry -> GetStoreProductsResDto.builder()
                        .storeId(storeId)
                        .category(entry.getKey().getCategoryName())
                        .displayOrder(entry.getKey().getDisplayOrder())
                        .products(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(GetStoreProductsResDto::getDisplayOrder))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetProductDetailResDto getProductDetail(Long storeId, Long productId){
        log.info("[ProductService] getProductDetail request received. storeId={}, productId={}", storeId, productId);

        if (!storeRepository.existsById(storeId)) {
            log.warn("[ProductService] Store not found. storeId={}", storeId);
            throw new BusinessException(StoreErrorCode.STORE_NOT_FOUND);
        }

        ProductStock stockFound = productStockRepository.getProductDetail(storeId, productId)
                .orElseThrow(() -> {
                    log.warn("[ProductService] Product not found in store. productId={}, storeId={}", productId, storeId);
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });

        Product productFound = stockFound.getProduct();

        return GetProductDetailResDto.builder()
                .productId(productId)
                .productName(productFound.getProductName())
                .unitAmount(productFound.getUnitAmount())
                .description(productFound.getDescription())
                .stock(stockFound.getStockQuantity())
                .imageUrl(productFound.getImageUrl())
                .discountRate(productFound.getDiscountRate())
                .build();
    }
}
