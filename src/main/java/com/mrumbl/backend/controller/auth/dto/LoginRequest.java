package com.mrumbl.backend.controller.auth.dto;

import com.mrumbl.backend.common.Sensitive;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Sensitive
    private String password;
}
