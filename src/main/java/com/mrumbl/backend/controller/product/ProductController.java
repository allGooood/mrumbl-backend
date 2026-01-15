package com.mrumbl.backend.controller.product;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.product.dto.GetStoreProductsResDto;
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
    private final ProductService productService;

    @GetMapping("/{storeId}")
    public Response<List<GetStoreProductsResDto>> getProducts(@PathVariable @Positive(message = "Store ID must be positive") Long storeId){
        return Response.ok(productService.getProducts(storeId));
    }
}
