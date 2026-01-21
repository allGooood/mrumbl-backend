package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreListResponse {
    private List<StoreInformationDto> stores;
}
