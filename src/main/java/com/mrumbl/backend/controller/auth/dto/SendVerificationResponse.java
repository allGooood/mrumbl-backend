package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SendVerificationResponse {
    private String email;
    private int ttlSeconds;
    private LocalDateTime issuedAt;
}
