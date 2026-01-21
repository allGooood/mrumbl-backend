package com.mrumbl.backend.service.cart.mapper;

import com.mrumbl.backend.controller.cart.dto.GetCartResDto;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.repository.product.ProductRepository;
import com.mrumbl.backend.repository.product.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartMapper {
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    private final CookieOptionMapper cookieOptionMapper;

    public GetCartResDto toGetCartResDto(RedisCart cart){
        // isSoldOut
        Product product = productRepository.findByIdAndInUse(cart.getProductId(), true)
                .orElse(null);
        ProductStock productStock = productStockRepository.findByStoreIdAndProductId(cart.getStoreId(), cart.getProductId())
                .orElse(null);
        Boolean isSoldOut = (product == null || productStock == null) ? true : productStock.getIsSoldOut();

        // TODO - Extra Price 붙은 경우
        BigDecimal unitAmount = BigDecimal.valueOf(product.getUnitAmount());
        BigDecimal productAmount = unitAmount.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return GetCartResDto.builder()
                .cartId(cart.getCartId())
                .productId(cart.getProductId())
                .productName(product.getProductName())
                .unitAmount(unitAmount)
                .productAmount(productAmount)
                .imageUrl(product.getImageUrl())
                .isSoldOut(isSoldOut)
                .requiredItemCount(product.getRequiredItemCount())
                .quantity(cart.getQuantity())
                .options(cookieOptionMapper.toCookieOptionDetailDto(cart.getOptions()))
                .build();
    }

}
