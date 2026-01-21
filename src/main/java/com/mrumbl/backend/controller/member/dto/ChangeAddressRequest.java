package com.mrumbl.backend.controller.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeAddressRequest {
    @NotBlank(message = "Address must not be blank")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @NotBlank(message = "Address detail must not be blank")
    @Size(max = 100, message = "Address detail must not exceed 100 characters")
    private String addressDetail;

    @NotBlank(message = "Postcode must not be blank")
    private String postcode;
}
