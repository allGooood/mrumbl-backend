package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerifyPasswordResDto {
    private boolean isVerified;
    private int attemptLeft;
}
