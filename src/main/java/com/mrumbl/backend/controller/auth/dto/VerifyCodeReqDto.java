package com.mrumbl.backend.controller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyCodeReqDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationCode;
}
