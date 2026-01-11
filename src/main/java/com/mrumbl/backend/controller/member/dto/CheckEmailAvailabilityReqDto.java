package com.mrumbl.backend.controller.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckEmailAvailabilityReqDto {

    @NotBlank
    @Email
    private String email;
}
