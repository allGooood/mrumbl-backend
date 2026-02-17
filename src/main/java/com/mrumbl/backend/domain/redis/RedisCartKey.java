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

    /** 이 사용자 카트가 담당하는 상점 ID. 카트가 비어 있으면 null. 한 사용자는 동시에 한 상점만 담을 수 있음. */
    private Long storeId;

    @Builder.Default
    private Set<String> cartIds = new HashSet<>();
}