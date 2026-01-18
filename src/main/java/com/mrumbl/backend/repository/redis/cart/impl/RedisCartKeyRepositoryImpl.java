package com.mrumbl.backend.repository.redis.cart.impl;

import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.redis.cart.RedisCartKeyRepository;
import com.mrumbl.backend.repository.redis.cart.RedisCartKeyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
@RequiredArgsConstructor
public class RedisCartKeyRepositoryImpl implements RedisCartKeyRepositoryCustom {
    private final RedisCartKeyRepository redisCartKeyRepository;

    @Override
    public RedisCartKey findByEmailOrCreateEmpty(String email) {
        return redisCartKeyRepository.findById(email)
                .orElse(RedisCartKey.builder()
                        .id(email)
                        .cartIds(new HashSet<>())
                        .build());
    }
}
