package com.mrumbl.backend.controller.store;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.store.dto.StoreListResponse;
import com.mrumbl.backend.service.StoreService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public Response<StoreListResponse> searchStores(@NotBlank(message = "keyword must not be blank")
                                                        String keyword){
        log.info("[StoreController] GET /api/stores invoked. keyword={}", keyword);

        keyword = keyword.trim();
        StoreListResponse response = storeService.searchStores(keyword);
        return Response.ok(response);
    }

}
