package com.mrumbl.backend.mvc.repository.redis;

import com.mrumbl.backend.mvc.domain.redis.RedisToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
