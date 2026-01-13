package com.mrumbl.backend.controller.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreListResponse {
    private List<StoreResponse> stores;

    @Getter
    @Builder
    public static class StoreResponse {
        private Long storeId;
        private String storeName;
        private AddressResponse storeAddress;
        private boolean isOpen;
    }

    @Getter
    @Builder
    public static class AddressResponse {
        private String address;
        private String addressDetail;
        private String postcode;
    }
}
