package com.mrumbl.backend.service.cart;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.common.exception.error_codes.CartErrorCode;
import com.mrumbl.backend.controller.cart.dto.*;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductCookie;
import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.domain.redis.RedisCartKey;
import com.mrumbl.backend.repository.MemberRepository;
import com.mrumbl.backend.repository.ProductCookieRepository;
import com.mrumbl.backend.repository.ProductRepository;
import com.mrumbl.backend.repository.ProductStockRepository;
import com.mrumbl.backend.repository.redis.RedisCartKeyRepository;
import com.mrumbl.backend.repository.redis.RedisCartRepository;
import com.mrumbl.backend.service.cart.validation.CartValidator;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.mrumbl.backend.common.util.RandomManager.createCartKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisCartKeyRepository redisCartKeyRepository;
    private final RedisCartRepository redisCartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductCookieRepository productCookieRepository;

    private final MemberValidator memberValidator;
    private final CartValidator cartValidator;

    public CartResDto addCarts(String email, AddCartReqDto reqDto) {
        Integer quantity = reqDto.getQuantity();
        // 수량 검증
        if (quantity == null || quantity < 1) {
            log.warn("Invalid cart quantity. email={}, quantity={}", email, quantity);
            throw new BusinessException(CartErrorCode.INVALID_CART_QUANTITY);
        }

        // RedisCartKey 조회
        RedisCartKey cartKeyFound = findRedisCartKeyByEmailOrCreateOne(email);

        // 같은 storeId와 productId를 가진 기존 카트 찾기
        Optional<RedisCart> existingCart = findCartByStoreIdAndProductId(cartKeyFound.getCartIds(), reqDto.getStoreId(), reqDto.getProductId());

        String cartId;
        Long productId;
        Long storeId;

        if (existingCart.isPresent()) {
            // 기존 카트 UPDATE
            RedisCart oldCart = existingCart.get();

            cartId = oldCart.getCartId();
            productId = oldCart.getProductId();
            storeId = oldCart.getStoreId();

            log.info("Updating existing cart. email={}, cartId={}, productId={}, storeId={}, quantity={}", 
                    email, cartId, productId, storeId, quantity);

        } else {
            // 새로운 카트 INSERT
            cartId = createCartKey();
            productId = reqDto.getProductId();
            storeId = reqDto.getStoreId();

            cartKeyFound.getCartIds().add(cartId);
            redisCartKeyRepository.save(cartKeyFound);

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

    public List<GetCartResDto> getCarts(String email) {
        // Member 검증
        memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Member not found or inactive. email={}", email);
                    return new BusinessException(AccountErrorCode.MEMBER_NOT_FOUND);
                });

        RedisCartKey cartKeyFound = findRedisCartKeyByEmailOrCreateOne(email);

        List<RedisCart> cartsFound = findAllByCartIds(cartKeyFound.getCartIds());
        log.info("Retrieving carts. email={}, cartCount={}", email, cartsFound.size());

        return cartsFound.stream()
                .map(cart -> convertToGetCartResDto(cart))
                .toList();
    }

    public CartResDto putCart(String email, PutCartReqDto reqDto) {
        log.info("Updating cart. email={}, cartId={}, quantity={}", email, reqDto.getCartId(), reqDto.getQuantity());

        Integer quantity = reqDto.getQuantity();
        String cartId = reqDto.getCartId();

        // Validation
        cartValidator.checkQuantityValidation(quantity);
        memberValidator.checkExistingMember(email);
        RedisCart cartFound = cartValidator.checkAndReturnExistingCart(cartId);

        RedisCartKey cartKeyFound = cartValidator.checkAndReturnCartKey(email);
        Set<String> cartIds = cartKeyFound.getCartIds();
        if (cartIds == null || !cartIds.contains(reqDto.getCartId())) {
            log.warn("Cart ownership validation failed. email={}, cartId={}", email, reqDto.getCartId());
            throw new BusinessException(CartErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 카트 업데이트
        RedisCart updatedCart = RedisCart.builder()
                .cartId(reqDto.getCartId())
                .productId(cartFound.getProductId())
                .storeId(cartFound.getStoreId())
                .quantity(quantity)
                .options(convertToCookieOptions(reqDto.getOptions()))
                .build();

        redisCartRepository.save(updatedCart);

        log.info("Cart updated successfully. email={}, cartId={}, quantity={}", email, reqDto.getCartId(), quantity);

        return CartResDto.builder()
                .cartIds(Collections.singletonList(reqDto.getCartId()))
                .build();
    }

    private GetCartResDto convertToGetCartResDto(RedisCart cart) {
        // isSoldOut
        Product product = productRepository.findByIdAndInUse(cart.getProductId(), true)
                .orElse(null);

        ProductStock productStock = productStockRepository.getProductDetail(cart.getStoreId(), cart.getProductId())
                .orElse(null);

        Boolean isSoldOut = (product == null || productStock == null) ? true : productStock.getIsSoldOut();

        // TODO - Extra Price 붙은 경우
        BigDecimal unitAmount = BigDecimal.valueOf(product.getUnitAmount());
        BigDecimal productAmount = unitAmount.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return GetCartResDto.builder()
                .cartId(cart.getCartId())
                .productId(cart.getProductId())
                .productName(product.getProductName())
                .unitAmount(unitAmount)
                .productAmount(productAmount)
                .imageUrl(product.getImageUrl())
                .isSoldOut(isSoldOut)
                .requiredItemCount(product.getRequiredItemCount())
                .quantity(cart.getQuantity())
                .options(convertToCookieOptionDetails(cart.getOptions()))
                .build();
    }

    private List<RedisCart.CookieOption> convertToCookieOptions(List<CookieOptionDto> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }

        return options.stream()
                .map(option -> RedisCart.CookieOption.builder()
                        .cookieId(option.getCookieId())
                        .quantity(option.getQuantity())
                        .build())
                .toList();
    }

    private List<CookieOptionDetailDto> convertToCookieOptionDetails(List<RedisCart.CookieOption> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }

        return options.stream()
                .map(option -> {
                    ProductCookie cookieFound = productCookieRepository.findById(option.getCookieId())
                            .orElse(null);

                    return CookieOptionDetailDto.builder()
                            .cookieId(option.getCookieId())
                            .cookieName(cookieFound != null ? cookieFound.getCookieName() : null)
                            .quantity(option.getQuantity())
                            .isSoldOut(cookieFound == null || !cookieFound.getInUse())
                            .build();
                })
                .toList();
    }

    private Optional<RedisCart> findCartByStoreIdAndProductId(Set<String> cartIds, Long storeId, Long productId) {
        if (CollectionUtils.isEmpty(cartIds)) {
            return Optional.empty();
        }

        return findAllByCartIds(cartIds).stream()
                .filter(cart -> cart.getStoreId().equals(storeId)
                                    && cart.getProductId().equals(productId))
                .findFirst();
    }

    private List<RedisCart> findAllByCartIds(Set<String> cartIds){
        Iterable<RedisCart> cartsIterable = redisCartRepository.findAllById(cartIds);
        List<RedisCart> carts = new ArrayList<>();
        cartsIterable.forEach(carts::add);
        return carts;
    }

    private RedisCartKey findRedisCartKeyByEmailOrCreateOne(String email){
        return redisCartKeyRepository.findById(email)
                .orElse(RedisCartKey.builder()
                        .id(email)
                        .cartIds(new HashSet<>())
                        .build());
    }
}
