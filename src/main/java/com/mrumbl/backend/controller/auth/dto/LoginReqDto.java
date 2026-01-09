package com.mrumbl.backend.controller.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginReqDto {
    private String email;
    private String password;
}
