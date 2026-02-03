package com.mrumbl.backend.service.product.mapper;

import com.mrumbl.backend.common.util.PriceConverter;
import com.mrumbl.backend.controller.product.dto.CookieProductsResponse;
import com.mrumbl.backend.controller.product.dto.ProductDetailResponse;
import com.mrumbl.backend.controller.product.dto.StoreProductsResponse;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductCookie;
import com.mrumbl.backend.domain.ProductStock;

/**
 * Product Entity <-> DTO
 */
public class ProductMapper {

    public static StoreProductsResponse.StoreProductDto toStoreProductDto(Product product, ProductStock stock){
        return StoreProductsResponse.StoreProductDto.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .unitAmount(product.getUnitAmount())
                .discountRate(product.getDiscountRate())
                .isSoldOut(stock.getIsSoldOut())
                .productType(product.getProductType().name())
                .imageUrl(product.getImageUrl())
                .requiredItemCount(product.getRequiredItemCount())
                .build();
    }

    public static ProductDetailResponse toProductDetailResponse(Product product, ProductStock stock){
        return ProductDetailResponse.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .unitAmount(product.getUnitAmount())
                .description(product.getDescription())
                .stock(stock.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .discountRate(product.getDiscountRate())
                .build();
    }

    public static CookieProductsResponse toCookieProductsResponse(ProductCookie cookie){
        return CookieProductsResponse.builder()
                .cookieId(cookie.getId())
                .cookieName(cookie.getCookieName())
                .imageUrl(cookie.getImageUrl())
                .cookieCalorie(cookie.getCookieCalorie())
                .additionalPrice(PriceConverter.centsToDollars(cookie.getAdditionalPrice()))
                .description(cookie.getDescription())
                .build();
    }
}
