package com.mrumbl.backend.service.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordValidationResult {
    private boolean isValid;
    private int attemptLeft;
}
