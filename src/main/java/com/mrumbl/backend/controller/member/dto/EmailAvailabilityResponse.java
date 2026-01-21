package com.mrumbl.backend.controller.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailAvailabilityResponse {
    private boolean isAvailable;
}
