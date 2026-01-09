package com.mrumbl.backend.mvc.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResDto {
    private String email;
}
