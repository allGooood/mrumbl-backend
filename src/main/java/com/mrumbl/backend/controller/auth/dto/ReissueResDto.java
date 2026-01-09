package com.mrumbl.backend.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueResDto {
    private String email;
    private String accessToken;
}
