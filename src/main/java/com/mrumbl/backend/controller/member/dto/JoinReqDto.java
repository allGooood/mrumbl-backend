package com.mrumbl.backend.controller.member.dto;

import lombok.Data;

@Data
public class JoinReqDto {
    private String email;
    private String password;
    private String name;
}
