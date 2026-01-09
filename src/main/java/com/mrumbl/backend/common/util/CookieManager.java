package com.mrumbl.backend.common.util;

import com.mrumbl.backend.common.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieManager {
    private final JwtProperties jwtProperties;
    private final static String COOKIE_NAME = "refreshToken";
    private final static String COOKIE_PATH = "/auth";
    private final static String COOKIE_SAME_SITE = "NONE";

    private ResponseCookie createCookie(String token, Duration ttl) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true) // JS 접근 불가
                .secure(true) // CSRF, 세션 하이재킹 방지
                .sameSite(COOKIE_SAME_SITE)
                .path(COOKIE_PATH)
                .maxAge(ttl)
                .build();
    }

    public ResponseCookie createValidCookie(String token){
        return createCookie(token,
                Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()));
    }

    public ResponseCookie createExpiredCookie(){
        return createCookie("",
                Duration.ofMillis(0));
    }

}
