package com.mrumbl.backend.repository.redis.cart;

import com.mrumbl.backend.domain.redis.RedisCart;

import java.util.Optional;
import java.util.Set;

public interface RedisCartRepositoryCustom {
    Optional<RedisCart> findByStoreIdAndProductId(Set<String> cartIds, Long storeId, Long productId);
}
