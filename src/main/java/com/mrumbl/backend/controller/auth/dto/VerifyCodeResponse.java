package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VerifyCodeResponse {
    private String email;
    private LocalDateTime verifiedAt;
}
