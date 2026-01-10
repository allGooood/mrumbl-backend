package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class VerifyPasswordResDto {
    private boolean verified;
    private int attemptLeft;
}
