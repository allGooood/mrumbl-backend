package com.mrumbl.backend.service;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.CartErrorCode;
import com.mrumbl.backend.controller.cart.dto.AddCartReqDto;
import com.mrumbl.backend.controller.cart.dto.CartResDto;
import com.mrumbl.backend.controller.cart.dto.CookieOptionDto;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.redis.RedisCartKeyRepository;
import com.mrumbl.backend.repository.redis.RedisCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mrumbl.backend.common.util.RandomManager.createCartKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisCartKeyRepository redisCartKeyRepository;
    private final RedisCartRepository redisCartRepository;

    public CartResDto addCarts(String email, AddCartReqDto reqDto) {
        Integer quantity = reqDto.getQuantity();
        // 수량 검증
        if (quantity == null || quantity < 1) {
            log.warn("Invalid cart quantity. email={}, quantity={}", email, quantity);
            throw new BusinessException(CartErrorCode.INVALID_CART_QUANTITY);
        }

        // CartKey 조회
        RedisCartKey cartKeyEntity = redisCartKeyRepository.findById(email)
                .orElse(RedisCartKey.builder()
                            .id(email)
                            .cartIds(new HashSet<>())
                        .build());

        // 같은 storeId와 productId를 가진 기존 카트 찾기
        Optional<RedisCart> existingCart = findExistingCart(cartKeyEntity.getCartIds(), reqDto.getStoreId(), reqDto.getProductId());

        String cartId;
        Long productId;
        Long storeId;

        if (existingCart.isPresent()) {
            // 기존 카트 UPDATE
            RedisCart existing = existingCart.get();

            cartId = existing.getCartId();
            productId = existing.getProductId();
            storeId = existing.getStoreId();

            log.info("Updating existing cart. email={}, cartId={}, productId={}, storeId={}, quantity={}", 
                    email, cartId, productId, storeId, quantity);

        } else {
            // 새로운 카트 INSERT
            cartId = createCartKey();
            productId = reqDto.getProductId();
            storeId = reqDto.getStoreId();

            cartKeyEntity.getCartIds().add(cartId);
            redisCartKeyRepository.save(cartKeyEntity);

            log.info("Creating new cart. email={}, cartId={}, productId={}, storeId={}, quantity={}", 
                    email, cartId, productId, storeId, quantity);
        }

        RedisCart cartEntity = RedisCart.builder()
                .cartId(cartId)
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .options(convertToCookieOptions(reqDto.getOptions()))
                .build();

        redisCartRepository.save(cartEntity);

        return CartResDto.builder()
                .cartIds(Collections.singletonList(cartId))
                .build();
    }

    /**
     * 같은 storeId와 productId를 가진 기존 카트 검색
     */
    private Optional<RedisCart> findExistingCart(Set<String> cartIds, Long storeId, Long productId) {
        if (cartIds == null || cartIds.isEmpty()) {
            return Optional.empty();
        }

        Iterable<RedisCart> cartsIterable = redisCartRepository.findAllById(cartIds);

        List<RedisCart> carts = new ArrayList<>();
        cartsIterable.forEach(carts::add);

        return carts.stream()
                .filter(cart -> cart.getStoreId().equals(storeId)
                                    && cart.getProductId().equals(productId))
                .findFirst();
    }

    /**
     * CookieOptionDto -> RedisCart.CookieOption
     */
    private List<RedisCart.CookieOption> convertToCookieOptions(List<CookieOptionDto> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .map(option -> RedisCart.CookieOption.builder()
                        .cookieId(option.getCookieId())
                        .quantity(option.getQuantity())
                        .build())
                .toList();
    }
}
