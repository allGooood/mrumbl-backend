package com.mrumbl.backend.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordRequest {
    @NotBlank
    private String password;
}
