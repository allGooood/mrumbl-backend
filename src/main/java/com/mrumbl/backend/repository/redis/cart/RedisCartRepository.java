package com.mrumbl.backend.repository.redis.cart;

import com.mrumbl.backend.domain.redis.RedisCart;
import org.springframework.data.repository.CrudRepository;

public interface RedisCartRepository extends CrudRepository<RedisCart, String> {
}
