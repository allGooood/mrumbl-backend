package com.mrumbl.backend.mvc.domain.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@RedisHash(value = "token")
public class RedisToken {
    @Id
    private String email; // TODO - 추후에 memberId로 변경

    private String refreshToken;

    @TimeToLive
    private Long ttl;
}
