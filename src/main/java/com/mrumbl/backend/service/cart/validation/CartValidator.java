package com.mrumbl.backend.service.cart.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.CartErrorCode;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.redis.RedisCartKeyRepository;
import com.mrumbl.backend.repository.redis.RedisCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartValidator {
    private final RedisCartRepository redisCartRepository;
    private final RedisCartKeyRepository redisCartKeyRepository;

    public void checkQuantityValidation(Integer quantity){
        if (quantity == null || quantity < 1) {
            throw new BusinessException(CartErrorCode.INVALID_CART_QUANTITY);
        }
    }

    public RedisCart checkAndReturnExistingCart(String cartId){
        return redisCartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart not found. cartId={}", cartId);
                    return new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
                });
    }

    public RedisCartKey checkAndReturnCartKey(String email){
        return redisCartKeyRepository.findById(email)
                .orElseThrow(() -> {
                    log.warn("User cart not found. email={}", email);
                    return new BusinessException(CartErrorCode.CART_NOT_FOUND);
                });
    }

    public void checkCartOwnershipValidation(){

    }
}
