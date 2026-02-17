
package com.mrumbl.backend.service.cart.validation;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.CartErrorCode;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.cart.RedisCartKeyRepository;
import com.mrumbl.backend.repository.cart.RedisCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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

    // TODO - return -> throw
    public RedisCart checkAndReturnExistingCart(String cartId){
        return redisCartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart not found. cartId={}", cartId);
                    return new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
                });
    }

    public List<RedisCart> checkAndReturnExistingCartAll(Set<String> cartIds){
        Iterable<RedisCart> cartsIterable = redisCartRepository.findAllById(cartIds);
        List<RedisCart> carts = new ArrayList<>();
        cartsIterable.forEach(carts::add);
        return carts;
    }

    public RedisCartKey checkAndReturnCartKey(String email){
        return redisCartKeyRepository.findById(email)
                .orElseThrow(() -> {
                    log.warn("User cart not found. email={}", email);
                    return new BusinessException(CartErrorCode.CART_NOT_FOUND);
                });
    }

    public void checkCartOwnershipValidation(Set<String> cartIdsFound, Collection<String> cartIdsRequested){
        if(cartIdsFound == null || !cartIdsFound.containsAll(cartIdsRequested)){
            log.warn("Cart ownership validation failed. cartIds={}", cartIdsRequested);
            throw new BusinessException(CartErrorCode.CART_NOT_FOUND);
        }
    }

    public void checkCartOwnershipValidation(Set<String> cartIdsFound, String cartIdRequested){
        checkCartOwnershipValidation(cartIdsFound, Collections.singleton(cartIdRequested));
    }

    /**
     * 한 사용자는 한 상점의 상품만 카트에 담을 수 있음.
     * 이미 다른 상점 상품이 담겨 있으면 예외.
     */
    public void checkStoreIdMatchesCartKey(RedisCartKey cartKey, Long requestStoreId) {
        if (cartKey.getStoreId() != null && !cartKey.getStoreId().equals(requestStoreId)) {
            log.warn("Cart store mismatch. cartKey.storeId={}, requestStoreId={}", cartKey.getStoreId(), requestStoreId);
            throw new BusinessException(CartErrorCode.CART_STORE_MISMATCH);
        }
    }
}
