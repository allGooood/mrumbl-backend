package com.mrumbl.backend.service.auth.mapper;

import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.controller.auth.dto.LoginResponse;
import com.mrumbl.backend.controller.auth.dto.ReissueResponse;
import com.mrumbl.backend.controller.auth.dto.VerifyPasswordResponse;
import com.mrumbl.backend.service.auth.dto.LoginResultInternal;
import com.mrumbl.backend.service.auth.dto.PasswordValidationResultInternal;

/**
 * Auth Service DTO <-> Controller DTO
 */
public class AuthMapper {

    public static LoginResponse toLoginResponse(LoginResultInternal loginResult) {
        return LoginResponse.builder()
                .email(loginResult.getEmail())
                .accessToken(loginResult.getAccessToken())
                .attemptLeft(loginResult.getAttemptLeft())
                .memberId(loginResult.getMemberId())
                .build();
    }

    public static ReissueResponse toReissueResponse(JwtToken tokens) {
        return ReissueResponse.builder()
                .email(tokens.getEmail())
                .accessToken(tokens.getAccessToken())
                .build();
    }

    public static VerifyPasswordResponse toVerifyPasswordResponse(PasswordValidationResultInternal result) {
        return VerifyPasswordResponse.builder()
                .isVerified(result.isValid())
                .attemptLeft(result.getAttemptLeft())
                .build();
    }
}
