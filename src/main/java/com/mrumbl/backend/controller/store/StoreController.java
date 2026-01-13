package com.mrumbl.backend.controller.store;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.store.dto.StoreListResponse;
import com.mrumbl.backend.controller.store.dto.GetStoreResDto;
import com.mrumbl.backend.service.StoreService;
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

    // TODO - param 없을 경우 전체조회 가능하도록 수정
    // TODO - 반환값에 x,y 좌표 추가
    @GetMapping
    public Response<StoreListResponse> searchStores(@NotBlank(message = "keyword must not be blank")
                                                        String keyword){
        log.info("[StoreController] GET /api/stores invoked. keyword={}", keyword);

        keyword = keyword.trim();
        StoreListResponse response = storeService.searchStores(keyword);
        return Response.ok(response);
    }

    @GetMapping("/{storeId}")
    public Response<GetStoreResDto> getStore(@PathVariable @Positive Long storeId){
        log.info("[StoreController] GET /api/stores/{storeId} invoked. storeId={}", storeId);

        GetStoreResDto response = storeService.getStore(storeId);
        return Response.ok(response);
    }

    @GetMapping("/nearby")
    public Response<StoreListResponse> getStoreNearby(@RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double x,
                                                      @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double y,
                                                      @RequestParam @Positive Integer r){
        log.info("[StoreController] GET /api/stores/nearby invoked. x={}, y={}, r={}", x, y, r);

        BigDecimal xCoordinate = BigDecimal.valueOf(x);
        BigDecimal yCoordinate = BigDecimal.valueOf(y);
        
        StoreListResponse response = storeService.getStoreNearby(xCoordinate, yCoordinate, r);
        return Response.ok(response);
    }

}
