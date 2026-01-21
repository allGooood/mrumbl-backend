package com.mrumbl.backend.controller.product;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.product.dto.CookieProductsResponse;
import com.mrumbl.backend.controller.product.dto.ProductDetailResponse;
import com.mrumbl.backend.controller.product.dto.StoreProductsResponse;
import com.mrumbl.backend.service.product.ProductService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private static final String STORE_ID_POSITIVE_MESSAGE = "Store ID must be positive";
    private static final String PRODUCT_ID_POSITIVE_MESSAGE = "Product ID must be positive";
    
    private final ProductService productService;

    @GetMapping("/{storeId}")
    public Response<List<StoreProductsResponse>> getProducts(
            @PathVariable @Positive(message = STORE_ID_POSITIVE_MESSAGE) Long storeId){
        return Response.ok(productService.getProducts(storeId));
    }

    @GetMapping("/{storeId}/{productId}")
    public Response<ProductDetailResponse> getProductDetail(
            @PathVariable @Positive(message = STORE_ID_POSITIVE_MESSAGE) Long storeId,
            @PathVariable @Positive(message = PRODUCT_ID_POSITIVE_MESSAGE) Long productId){
        return Response.ok(productService.getProductDetail(storeId, productId));
    }

    @GetMapping("/cookies")
    public Response<List<CookieProductsResponse>> getCookies(){
        return Response.ok(productService.getCookies());
    }

}
