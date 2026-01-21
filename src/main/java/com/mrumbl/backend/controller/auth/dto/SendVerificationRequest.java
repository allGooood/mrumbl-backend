package com.mrumbl.backend.controller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendVerificationRequest {

    @NotBlank
    @Email
    private String email;
}
