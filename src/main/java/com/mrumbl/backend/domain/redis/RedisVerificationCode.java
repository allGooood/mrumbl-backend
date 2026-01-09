package com.mrumbl.backend.domain.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@RedisHash(value = "verification_code", timeToLive = 180)
public class RedisVerificationCode {
    @Id
    private String email;
    private String verificationCode;
}
