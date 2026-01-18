package com.mrumbl.backend.repository.redis.cart.impl;

import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.redis.cart.RedisCartKeyRepository;
import com.mrumbl.backend.repository.redis.cart.RedisCartRepositoryCustom;
import com.mrumbl.backend.service.cart.validation.CartValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisCartRepositoryImpl implements RedisCartRepositoryCustom {
    private CartValidator cartValidator;
    private RedisCartKeyRepository redisCartKeyRepository;

    @Override
    public Optional<RedisCart> findByStoreIdAndProductId(Set<String> cartIds, Long storeId, Long productId) {
        if (CollectionUtils.isEmpty(cartIds)) {
            return Optional.empty();
        }

        return cartValidator.checkAndReturnExistingCartAll(cartIds).stream()
                .filter(cart -> cart.getStoreId().equals(storeId)
                        && cart.getProductId().equals(productId))
                .findFirst();
    }

}
