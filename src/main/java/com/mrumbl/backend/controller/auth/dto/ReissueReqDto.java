package com.mrumbl.backend.controller.auth.dto;

import lombok.Data;

@Data
public class ReissueReqDto {
    private String email;
    private String refreshToken;
}
