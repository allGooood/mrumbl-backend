package com.mrumbl.backend.service.cart.mapper;

import com.mrumbl.backend.controller.cart.dto.CookieOptionDetailResponse;
import com.mrumbl.backend.controller.cart.dto.CookieOptionRequest;
import com.mrumbl.backend.domain.ProductCookie;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.repository.product.ProductCookieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CookieOptionMapper {
    private final ProductCookieRepository productCookieRepository;


    public List<RedisCart.CookieOption> toCookieOptions(List<CookieOptionRequest> options) {
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

    public List<CookieOptionDetailResponse> toCookieOptionDetailResponse(List<RedisCart.CookieOption> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }

        return options.stream()
                .map(option -> {
                    ProductCookie cookieFound = productCookieRepository.findById(option.getCookieId())
                            .orElse(null);

                    return CookieOptionDetailResponse.builder()
                            .cookieId(option.getCookieId())
                            .cookieName(cookieFound != null ? cookieFound.getCookieName() : null)
                            .quantity(option.getQuantity())
                            .isSoldOut(cookieFound == null || !cookieFound.getInUse())
                            .build();
                })
                .toList();
    }

    public int calculateOptionsExtraCents(List<RedisCart.CookieOption> options) {
        if (CollectionUtils.isEmpty(options)) {
            return 0;
        }
        int total = 0;
        for (RedisCart.CookieOption opt : options) {
            ProductCookie cookie = productCookieRepository.findById(opt.getCookieId()).orElse(null);
            if (cookie != null && cookie.getAdditionalPrice() != null) {
                total += cookie.getAdditionalPrice() * opt.getQuantity();
            }
        }
        return total;
    }
}
