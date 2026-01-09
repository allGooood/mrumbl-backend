package com.mrumbl.backend.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JwtUser {
    private Long memberId;
    private String email;
}
