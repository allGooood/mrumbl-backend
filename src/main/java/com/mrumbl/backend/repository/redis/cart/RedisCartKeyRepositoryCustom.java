package com.mrumbl.backend.repository.redis.cart;

import com.mrumbl.backend.domain.redis.RedisCartKey;

public interface RedisCartKeyRepositoryCustom {
    RedisCartKey findByEmailOrCreateEmpty(String email);

}
