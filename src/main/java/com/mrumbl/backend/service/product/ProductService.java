package com.mrumbl.backend.service.product;

import com.mrumbl.backend.common.enumeration.ProductCategory;
import com.mrumbl.backend.controller.product.dto.CookieProductsResponse;
import com.mrumbl.backend.controller.product.dto.ProductDetailResponse;
import com.mrumbl.backend.controller.product.dto.StoreProductsResponse;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.repository.product.ProductCookieRepository;
import com.mrumbl.backend.repository.product.ProductStockRepository;
import com.mrumbl.backend.service.product.mapper.ProductMapper;
import com.mrumbl.backend.service.product.validation.ProductValidator;
import com.mrumbl.backend.service.store.validation.StoreValidator;
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
    private final ProductCookieRepository productCookieRepository;

    private final StoreValidator storeValidator;
    private final ProductValidator productValidator;


    @Transactional(readOnly = true)
    public List<StoreProductsResponse> getProducts(Long storeId){
        storeValidator.checkExistingStore(storeId);

        List<ProductStock> stocksFound = productStockRepository.findByStoreIdWithFetchJoin(storeId);
        log.info("Found {} products for storeId={}", stocksFound.size(), storeId);

        Map<ProductCategory, List<StoreProductsResponse.StoreProductDto>> productsByCategory
                = groupProductsByCategory(stocksFound);

        return toStoreProductsResponse(storeId, productsByCategory);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long storeId, Long productId){
        storeValidator.checkExistingStore(storeId);

        ProductStock stockFound = productValidator.checkAndReturnProductStock(storeId, productId);
        Product productFound = stockFound.getProduct();
        log.info("Product found. storeId={}, productId={}, productName={}", 
                storeId, productId, productFound.getProductName());

        return ProductMapper.toProductDetailResponse(productFound, stockFound);
    }

    @Transactional(readOnly = true)
    public List<CookieProductsResponse> getCookies(){
        List<CookieProductsResponse> cookiesFound = productCookieRepository.findAllByInUse(true)
                .stream()
                .map(ProductMapper::toCookieProductsResponse)
                .toList();
        log.info("Found {} cookies", cookiesFound.size());

        return cookiesFound;
    }

    private Map<ProductCategory, List<StoreProductsResponse.StoreProductDto>> groupProductsByCategory(
            List<ProductStock> stocks) {
        return stocks.stream()
                .collect(Collectors.groupingBy(
                        stock -> stock.getProduct().getProductCategory(),
                        Collectors.mapping(
                                stock -> ProductMapper.toStoreProductDto(stock.getProduct(), stock),
                                Collectors.toList()
                        )
                ));
    }

    private List<StoreProductsResponse> toStoreProductsResponse(
            Long storeId,
            Map<ProductCategory, List<StoreProductsResponse.StoreProductDto>> productsByCategory) {
        return productsByCategory.entrySet().stream()
                .map(entry -> StoreProductsResponse.builder()
                        .storeId(storeId)
                        .category(entry.getKey().getCategoryName())
                        .displayOrder(entry.getKey().getDisplayOrder())
                        .products(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(StoreProductsResponse::getDisplayOrder))
                .collect(Collectors.toList());
    }
}
