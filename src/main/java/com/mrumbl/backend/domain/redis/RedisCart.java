package com.mrumbl.backend.domain.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Builder
@Getter
@RedisHash("cart")
public class RedisCart {
    @Id
    private String cartId;

    private Long productId;
    private Long storeId;
    private Integer quantity;
    private List<CookieOption> options;

    @Builder
    @Getter
    public static class CookieOption {
        private Long cookieId;
        private Integer quantity;
    }
}
