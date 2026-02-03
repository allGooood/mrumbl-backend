package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponse {
    private String email;
    private String accessToken;
    private int attemptLeft;
    private Long memberId;
}
