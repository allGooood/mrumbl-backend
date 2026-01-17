package com.mrumbl.backend.repository.redis;

import com.mrumbl.backend.domain.redis.RedisCartKey;
import org.springframework.data.repository.CrudRepository;

public interface RedisCartKeyRepository extends CrudRepository<RedisCartKey, String> {
}
