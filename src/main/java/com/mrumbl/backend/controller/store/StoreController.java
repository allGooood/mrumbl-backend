package com.mrumbl.backend.controller.store;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.store.dto.StoreListResponse;
import com.mrumbl.backend.controller.store.dto.StoreDetailResponse;
import com.mrumbl.backend.service.store.StoreService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public Response<StoreListResponse> searchStores(@NotBlank(message = "keyword must not be blank")
                                                        String keyword){
        return Response.ok(storeService.searchStores(keyword));
    }

    @GetMapping("/{storeId}")
    public Response<StoreDetailResponse> getStore(@PathVariable @Positive Long storeId){
        return Response.ok(storeService.getStore(storeId));
    }

    @GetMapping("/nearby")
    public Response<StoreListResponse> getNearbyStores(
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @Positive Integer radius){
        return Response.ok(storeService.getNearbyStores(
                BigDecimal.valueOf(longitude), 
                BigDecimal.valueOf(latitude), 
                radius));
    }

}
