package com.mrumbl.backend.domain.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "cart:user")
public class RedisCartKey {

    @Id // user:email, guest:uuid
    private String id;

    @Builder.Default
    private Set<String> cartIds = new HashSet<>();
}