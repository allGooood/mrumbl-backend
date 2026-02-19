package com.mrumbl.backend.common.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.ErrorResponse;
import com.mrumbl.backend.common.exception.error_codes.CommonErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    // 401 (UnAuthorized) Error Handling.
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("Request Uri : {}", request.getRequestURI());

        ErrorResponse body = ErrorResponse.builder()
                .success(false)
                .errorCode(CommonErrorCode.UNAUTHORIZED.getCode())
                .message(CommonErrorCode.UNAUTHORIZED.getMessage())
                .build();

        String responseBody = objectMapper.writeValueAsString(body);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
