package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SendVerificationResDto {
    private String email;
    private int ttlSeconds;
    private LocalDateTime issuedAt;
}
