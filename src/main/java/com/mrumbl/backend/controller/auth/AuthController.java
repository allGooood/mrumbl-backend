package com.mrumbl.backend.controller.auth;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.controller.auth.dto.*;
import com.mrumbl.backend.service.AuthService;
import com.mrumbl.backend.common.util.CookieManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieManager cookieManager;

    @PostMapping("/login/email")
    public Response<LoginResDto> login(@RequestBody LoginReqDto reqDto,
                                       HttpServletResponse response) {

        JwtToken tokens = authService.login(reqDto);

        ResponseCookie cookie = cookieManager.createValidCookie(tokens.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(LoginResDto.builder()
                        .accessToken(tokens.getAccessToken())
                        .email(tokens.getEmail())
                .build());
    }

    @DeleteMapping("/logout")
    public Response<LogoutResDto> logout(@RequestParam String email,
                                         HttpServletResponse response){

        // 1. Service에서 Redis의 RefreshToken 삭제
        LogoutResDto resDto = authService.logout(email);

        // 2. Cookie value="", maxAge(0) 으로 설정해서 클라이언트 전달
        ResponseCookie cookie = cookieManager.createExpiredCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(resDto);
    }

    @PostMapping("/reissue")
    public Response<ReissueResDto> reissue(@RequestBody ReissueReqDto reqDto,
                                           HttpServletResponse response){
        JwtToken tokens = authService.reissue(reqDto.getEmail(), reqDto.getRefreshToken());

        ResponseCookie cookie = cookieManager.createValidCookie(tokens.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(ReissueResDto.builder()
                        .email(tokens.getEmail())
                        .accessToken(tokens.getAccessToken())
                .build());
    }

    @PostMapping("/verification-code")
    public Response<SendVerificationResDto> sendVerificationCode(@Valid @RequestBody SendVerificationReqDto reqDto){
        SendVerificationResDto resDto = authService.sendVerificationCode(reqDto.getEmail());
        return Response.ok(resDto);
    }

    @PostMapping("/verification-code/verify")
    public void verifyVerificationCode(){

    }
}














