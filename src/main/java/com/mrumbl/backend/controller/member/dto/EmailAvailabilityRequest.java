package com.mrumbl.backend.controller.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailAvailabilityRequest {

    @NotBlank
    @Email
    private String email;
}
