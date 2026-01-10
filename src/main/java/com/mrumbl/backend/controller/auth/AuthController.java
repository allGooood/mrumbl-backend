package com.mrumbl.backend.controller.auth;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.auth.dto.*;
import com.mrumbl.backend.service.auth.AuthService;
import com.mrumbl.backend.common.util.CookieManager;
import com.mrumbl.backend.service.auth.dto.LoginResult;
import com.mrumbl.backend.service.auth.dto.PasswordValidationResult;
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
    public Response<LoginResDto> login(@RequestBody LoginReqDto reqDto,
                                       HttpServletResponse response) {

        LoginResult loginResult = authService.login(reqDto);

        ResponseCookie cookie = cookieManager.createValidCookie(loginResult.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(LoginResDto.builder()
                        .accessToken(loginResult.getAccessToken())
                        .email(loginResult.getEmail())
                        .attemptLeft(loginResult.getAttemptLeft())
                .build());
    }

    @DeleteMapping("/logout")
    public Response<Void> logout(@RequestParam String email,
                                         HttpServletResponse response){

        // 1. Service에서 Redis의 RefreshToken 삭제
        authService.logout(email);

        // 2. Cookie value="", maxAge(0) 으로 설정해서 클라이언트 전달
        ResponseCookie cookie = cookieManager.createExpiredCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.ok(null);
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
    public Response<VerifyCodeResDto> verifyVerificationCode(@Valid @RequestBody VerifyCodeReqDto reqDto){
        VerifyCodeResDto resDto = authService.verifyVerificationCode(reqDto.getEmail(), reqDto.getVerificationCode());
        return Response.ok(resDto);
    }

    @PostMapping("/password/verify")
    public Response<VerifyPasswordResDto> verifyPassword(@AuthenticationPrincipal JwtUser user,
                                                         @Valid @RequestBody VerifyPasswordReqDto reqDto){
        PasswordValidationResult passwordValidationResult = authService.verifyPassword(user, reqDto.getPassword());

        System.out.println("controler " + passwordValidationResult.getAttemptLeft());
        return Response.ok(VerifyPasswordResDto.builder()
                        .isVerified(passwordValidationResult.isValid())
                        .attemptLeft(passwordValidationResult.getAttemptLeft())
                .build());
    }
}














