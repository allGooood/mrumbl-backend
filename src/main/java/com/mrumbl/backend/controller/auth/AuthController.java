package com.mrumbl.backend.controller.auth;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.auth.dto.*;
import com.mrumbl.backend.service.auth.AuthService;
import com.mrumbl.backend.common.util.CookieManager;
import com.mrumbl.backend.service.auth.mapper.AuthMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieManager cookieManager;

    @PostMapping("/login/email")
    public Response<LoginResponse> login(@RequestBody LoginRequest reqDto,
                                         HttpServletResponse response) {
        var loginResult = authService.login(reqDto);

        ResponseCookie cookie = cookieManager.createValidCookie(loginResult.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(AuthMapper.toLoginResponse(loginResult));
    }

    @DeleteMapping("/logout")
    public Response<Void> logout(@AuthenticationPrincipal JwtUser user,
                                         HttpServletResponse response){
        // 1. Service에서 Redis의 RefreshToken 삭제
        authService.logout(user.getEmail());

        // 2. Cookie value="", maxAge(0) 으로 설정해서 클라이언트 전달
        ResponseCookie cookie = cookieManager.createExpiredCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(null);
    }

    @PostMapping("/reissue")
    public Response<ReissueResponse> reissue(@RequestBody ReissueRequest reqDto,
                                             HttpServletResponse response){
        JwtToken tokens = authService.reissue(reqDto.getEmail(), reqDto.getRefreshToken());

        ResponseCookie cookie = cookieManager.createValidCookie(tokens.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(AuthMapper.toReissueResponse(tokens));
    }

    @PostMapping("/verification-code")
    public Response<SendVerificationResponse> sendVerificationCode(@Valid @RequestBody SendVerificationRequest reqDto){
        return Response.ok(authService.sendVerificationCode(reqDto.getEmail()));
    }

    @PostMapping("/verification-code/verify")
    public Response<VerifyCodeResponse> verifyVerificationCode(@Valid @RequestBody VerifyCodeRequest reqDto){
        return Response.ok(authService.verifyVerificationCode(reqDto.getEmail(), reqDto.getVerificationCode()));
    }

    @PostMapping("/password/verify")
    public Response<VerifyPasswordResponse> verifyPassword(@AuthenticationPrincipal JwtUser user,
                                                           @Valid @RequestBody VerifyPasswordRequest reqDto){
        var passwordValidationResult = authService.verifyPassword(user.getEmail(), reqDto.getPassword());
        return Response.ok(AuthMapper.toVerifyPasswordResponse(passwordValidationResult));
    }
}














