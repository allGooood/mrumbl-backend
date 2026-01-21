package com.mrumbl.backend.service.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResultInternal {
    private String accessToken;
    private String refreshToken;
    private int attemptLeft;
    private String email;
}
