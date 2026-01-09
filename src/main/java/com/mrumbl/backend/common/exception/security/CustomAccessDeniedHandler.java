package com.mrumbl.backend.common.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrumbl.backend.common.exception.ErrorResponse;
import com.mrumbl.backend.common.exception.error_codes.CommonErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    // 403 (Forbidden) Error Handling.
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("Request Uri : {}", request.getRequestURI());

        ErrorResponse body = ErrorResponse.builder()
                .success(false)
                .errorCode(CommonErrorCode.FORBIDDEN.getCode())
                .message(CommonErrorCode.FORBIDDEN.getMessage())
                .build();
        String responseBody = objectMapper.writeValueAsString(body);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
