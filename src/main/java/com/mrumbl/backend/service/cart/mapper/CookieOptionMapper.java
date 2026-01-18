package com.mrumbl.backend.service.cart.mapper;

import com.mrumbl.backend.controller.cart.dto.CookieOptionDetailDto;
import com.mrumbl.backend.controller.cart.dto.CookieOptionDto;
import com.mrumbl.backend.domain.ProductCookie;
import com.mrumbl.backend.domain.redis.RedisCart;
import com.mrumbl.backend.repository.ProductCookieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CookieOptionMapper {
    private final ProductCookieRepository productCookieRepository;


    public List<RedisCart.CookieOption> toCookieOptions(List<CookieOptionDto> options) {
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

    public List<CookieOptionDetailDto> toCookieOptionDetailDto(List<RedisCart.CookieOption> options) {
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
}
