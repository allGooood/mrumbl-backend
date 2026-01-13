package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreAddressDto {
    private String address;
    private String addressDetail;
    private String postcode;
}
