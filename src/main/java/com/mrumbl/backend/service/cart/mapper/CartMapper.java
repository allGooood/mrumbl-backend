package com.mrumbl.backend.service.cart.mapper;

import com.mrumbl.backend.common.enumeration.ProductType;
import com.mrumbl.backend.controller.cart.dto.GetCartResponse;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.repository.product.ProductRepository;
import com.mrumbl.backend.repository.product.ProductStockRepository;
import com.mrumbl.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartMapper {
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    private final CookieOptionMapper cookieOptionMapper;

    public GetCartResponse toGetCartResponse(RedisCart cart){
        // isSoldOut
        Product product = productRepository.findByIdAndInUse(cart.getProductId(), true)
                .orElse(null);
        ProductStock productStock = productStockRepository.findByStoreIdAndProductIdWithFetchJoin(cart.getStoreId(), cart.getProductId())
                .orElse(null);
        Boolean isSoldOut = product == null || productStock == null || productStock.getIsSoldOut();

        if (product == null) {
            return GetCartResponse.builder()
                    .cartId(cart.getCartId())
                    .productId(cart.getProductId())
                    .productName(null)
                    .unitAmount(BigDecimal.ZERO)
                    .productAmount(BigDecimal.ZERO)
                    .imageUrl(null)
                    .isSoldOut(true)
                    .requiredItemCount(null)
                    .quantity(cart.getQuantity())
                    .options(cookieOptionMapper.toCookieOptionDetailResponse(cart.getOptions()))
                    .build();
        }

        int baseCents = product.getUnitAmount();
        int optionsExtraCents = product.getProductType() == ProductType.COOKIE_BOX
                ? cookieOptionMapper.calculateOptionsExtraCents(cart.getOptions())
                : 0;
        BigDecimal unitAmount = BigDecimal.valueOf(baseCents + optionsExtraCents);
        BigDecimal productAmount = unitAmount.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return GetCartResponse.builder()
                .cartId(cart.getCartId())
                .productId(cart.getProductId())
                .productName(product.getProductName())
                .unitAmount(unitAmount)
                .productAmount(productAmount)
                .imageUrl(S3Service.completeImageUrl(product.getImageUrl()))
                .isSoldOut(isSoldOut)
                .requiredItemCount(product.getRequiredItemCount())
                .quantity(cart.getQuantity())
                .options(cookieOptionMapper.toCookieOptionDetailResponse(cart.getOptions()))
                .build();
    }

}
