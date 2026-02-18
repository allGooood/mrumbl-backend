package com.mrumbl.backend.controller.cart.v2;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.cart.dto.*;
import com.mrumbl.backend.service.cart.v2.CartServiceV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/carts")
public class CartControllerV2 {
    private final CartServiceV2 cartServiceV2;

    @PostMapping
    public Response<GetCartResponse> addCarts(
            @AuthenticationPrincipal JwtUser user,
            @RequestBody @Valid AddCartRequest reqDto) {
        return Response.ok(cartServiceV2.addCarts(user.getEmail(), reqDto));
    }

    @GetMapping
    public Response<GetCartResponse> getCarts(@AuthenticationPrincipal JwtUser user){
        return Response.ok(cartServiceV2.getCarts(user.getEmail()));
    }

    @PutMapping
    public Response<GetCartResponse> modifyCart(@AuthenticationPrincipal JwtUser user,
                                                   @Valid @RequestBody PutCartRequest reqDto){
        return Response.ok(cartServiceV2.putCart(user.getEmail(), reqDto));
    }

    @DeleteMapping
    public Response<GetCartResponse> deleteCarts(@AuthenticationPrincipal JwtUser user,
                                                    @Valid @RequestBody DeleteCartRequest reqDto){
        return Response.ok(cartServiceV2.deleteCarts(user.getEmail(), reqDto));
    }

}