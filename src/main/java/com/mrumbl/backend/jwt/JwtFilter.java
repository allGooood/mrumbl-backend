package com.mrumbl.backend.jwt;

import com.mrumbl.backend.exception.BusinessException;
import com.mrumbl.backend.exception.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String accessToken = authorization.substring(7);

            if (StringUtils.hasText(accessToken)) {
                try {
                    tokenManager.validateToken(accessToken);
                    Authentication authentication = tokenManager.createAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (BusinessException e) {
                    request.setAttribute("authError", e);
                    authenticationEntryPoint.commence(request, response, null);
                    return; // 예외 발생 시 필터 체인 진행 중단
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
