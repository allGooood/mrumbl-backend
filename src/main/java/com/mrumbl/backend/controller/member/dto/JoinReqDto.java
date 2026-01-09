package com.mrumbl.backend.controller.member.dto;

import lombok.*;

@Setter
@Getter
public class JoinReqDto {
    private String email;
    private String password;
    private String name;
}
