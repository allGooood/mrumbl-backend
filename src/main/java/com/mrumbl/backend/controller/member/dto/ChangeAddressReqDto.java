package com.mrumbl.backend.controller.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeAddressReqDto {
    @NotBlank
    private String address;

    @NotBlank
    private String addressDetail;

    @NotBlank
    private String postcode;
}
