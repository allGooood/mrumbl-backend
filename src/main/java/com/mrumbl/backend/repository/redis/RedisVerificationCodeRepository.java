package com.mrumbl.backend.repository.redis;

import com.mrumbl.backend.domain.redis.RedisVerificationCode;
import org.springframework.data.repository.CrudRepository;

public interface RedisVerificationCodeRepository extends CrudRepository<RedisVerificationCode, String> {
}
