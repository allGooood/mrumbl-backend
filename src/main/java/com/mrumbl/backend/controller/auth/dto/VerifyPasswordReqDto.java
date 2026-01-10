package com.mrumbl.backend.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordReqDto {
    @NotBlank
    private String password;
}
