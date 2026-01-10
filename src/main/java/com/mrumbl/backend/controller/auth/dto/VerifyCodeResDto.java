package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VerifyCodeResDto {
    private String email;
    private LocalDateTime verifiedAt;
}
