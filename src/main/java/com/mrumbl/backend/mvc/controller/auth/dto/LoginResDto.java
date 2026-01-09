package com.mrumbl.backend.mvc.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResDto {
    private String email;
    private String accessToken;
}
