package com.mrumbl.backend.mvc.controller.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestDto {
    private String accessToken;
    private String refreshToken;
}
