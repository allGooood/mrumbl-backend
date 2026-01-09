package com.mrumbl.backend.repository.redis;

import com.mrumbl.backend.domain.redis.RedisToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
