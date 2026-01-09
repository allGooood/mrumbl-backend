package com.mrumbl.backend.mvc.controller.member.dto;

import lombok.*;

@Setter
@Getter
public class JoinReqDto {
    private String email;
    private String password;
    private String name;
}
