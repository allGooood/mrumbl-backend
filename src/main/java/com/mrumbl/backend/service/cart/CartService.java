package com.mrumbl.backend.service.cart;

import com.mrumbl.backend.common.enumeration.ProductType;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.CartErrorCode;
import com.mrumbl.backend.controller.cart.dto.*;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.cart.RedisCartKeyRepository;
import com.mrumbl.backend.repository.cart.RedisCartRepository;
import com.mrumbl.backend.service.cart.mapper.CartMapper;
import com.mrumbl.backend.service.cart.mapper.CookieOptionMapper;
import com.mrumbl.backend.service.cart.validation.CartValidator;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.mrumbl.backend.common.util.RandomManager.createCartKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisCartKeyRepository redisCartKeyRepository;
    private final RedisCartRepository redisCartRepository;

    private final MemberValidator memberValidator;
    private final CartValidator cartValidator;

    private final CartMapper cartMapper;
    private final CookieOptionMapper cookieOptionMapper;


    public List<GetCartResponse> getCarts(String email) {

        memberValidator.checkExistingMember(email);

        RedisCartKey cartKeyFound = findCartKeyOrCreateEmpty(email);
        
//        if (cartKeyFound.getStoreId() == null || !cartKeyFound.getStoreId().equals(storeId)) {
//            return List.of();
//        }
        List<RedisCart> cartsFound = cartValidator.checkAndReturnExistingCartAll(cartKeyFound.getCartIds());

        return cartsFound.stream()
                .map(cartMapper::toGetCartResponse)
                .toList();
    }

    public CartCommonResponse putCart(String email, PutCartRequest reqDto) {
        log.info("Updating cart. email={}, cartId={}, quantity={}", email, reqDto.getCartId(), reqDto.getQuantity());

        Integer quantity = reqDto.getQuantity();
        String cartId = reqDto.getCartId();

        // 1. Validation
        cartValidator.checkQuantityValidation(quantity);
        memberValidator.checkExistingMember(email);
        RedisCart cartFound = cartValidator.checkAndReturnExistingCart(cartId);
        RedisCartKey cartKeyFound = cartValidator.checkAndReturnCartKey(email);
        cartValidator.checkCartOwnershipValidation(cartKeyFound.getCartIds(), reqDto.getCartId());

        // 2. 카트 업데이트
        RedisCart updatedCart = RedisCart.builder()
                .cartId(reqDto.getCartId())
                .productId(cartFound.getProductId())
                .storeId(cartFound.getStoreId())
                .quantity(quantity)
                .options(cookieOptionMapper.toCookieOptions(reqDto.getOptions()))
                .build();

        redisCartRepository.save(updatedCart);

        log.info("Cart updated successfully. email={}, cartId={}, quantity={}", email, reqDto.getCartId(), quantity);

        return CartCommonResponse.builder()
                .cartIds(Collections.singleton(reqDto.getCartId()))
                .build();
    }

    public CartCommonResponse addCarts(String email, AddCartRequest reqDto) {
        Integer quantity = reqDto.getQuantity();

        cartValidator.checkQuantityValidation(quantity);

        RedisCartKey cartKeyFound = findCartKeyOrCreateEmpty(email);
        cartValidator.checkStoreIdMatchesCartKey(cartKeyFound, reqDto.getStoreId());

        // 1. 기존 카트 있는지 조회
        Optional<RedisCart> cartOptional = findByStoreIdAndProductId(cartKeyFound.getCartIds(), reqDto.getStoreId(), reqDto.getProductId());

        String cartId;
        Long productId;
        Long storeId;

        if (cartOptional.isPresent()
                && !reqDto.getProductType().equals(ProductType.COOKIE_BOX.name())) {

            // 2. 기존 카트 UPDATE
            RedisCart cartFound = cartOptional.get();

            cartId = cartFound.getCartId();
            productId = cartFound.getProductId();
            storeId = cartFound.getStoreId();
            quantity = cartFound.getQuantity() + reqDto.getQuantity();

            log.info("Updating existing cart. email={}, cartId={}, productId={}, storeId={}, quantity={}",
                    email, cartId, productId, storeId, quantity);

        } else {
            // 3. 새로운 카트 INSERT
            cartId = createCartKey();
            productId = reqDto.getProductId();
            storeId = reqDto.getStoreId();

            cartKeyFound.getCartIds().add(cartId);
            if (cartKeyFound.getStoreId() == null) {
                cartKeyFound.setStoreId(reqDto.getStoreId());
            }
            redisCartKeyRepository.save(cartKeyFound);

            log.info("Creating new cart. email={}, cartId={}, productId={}, storeId={}, quantity={}",
                    email, cartId, productId, storeId, quantity);
        }

        RedisCart cartEntity = RedisCart.builder()
                .cartId(cartId)
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .options(cookieOptionMapper.toCookieOptions(reqDto.getOptions()))
                .build();

        redisCartRepository.save(cartEntity);

        return CartCommonResponse.builder()
                .cartIds(Collections.singleton(cartId))
                .build();
    }

    public CartCommonResponse deleteCarts(String email, DeleteCartRequest reqDto){
        log.info("Deleting cart. email={}, cartIds={}", email, reqDto.getCartIds());

        memberValidator.checkExistingMember(email);
        deleteCartAndCartKey(email, reqDto.getCartIds());

        log.info("Cart deleted successfully. email={}, cartIds={}", email, reqDto.getCartIds());

        return CartCommonResponse.builder()
                .cartIds(reqDto.getCartIds())
                .build();
    }

    public void deleteCartAndCartKey(String email, Set<String> cartIds){
        RedisCartKey cartKeyFound = cartValidator.checkAndReturnCartKey(email);
        Set<String> cartIdsFound = cartKeyFound.getCartIds();
        cartValidator.checkCartOwnershipValidation(cartIdsFound, cartIds);

        List<RedisCart> cartsFound = cartValidator.checkAndReturnExistingCartAll(cartIds);
        if(cartsFound.size() != cartIds.size()){
            throw new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 2. 카트 삭제 (RedisCart, RedisCartKey)
        redisCartRepository.deleteAllById(cartIds);

        cartIdsFound.removeAll(cartIds);
        if (cartIdsFound.isEmpty()) {
            cartKeyFound.setStoreId(null);
        }
        redisCartKeyRepository.save(cartKeyFound);
    }

    private RedisCartKey findCartKeyOrCreateEmpty(String email) {
        return redisCartKeyRepository.findById(email)
                .orElse(RedisCartKey.builder()
                        .id(email)
                        .cartIds(new HashSet<>())
                        .build());
    }

    private Optional<RedisCart> findByStoreIdAndProductId(Set<String> cartIds, Long storeId, Long productId) {
        if (CollectionUtils.isEmpty(cartIds)) {
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
}
