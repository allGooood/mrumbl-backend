package com.mrumbl.backend.controller.cart;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.cart.dto.*;
import com.mrumbl.backend.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public Response<CartResDto> addCarts(
            @AuthenticationPrincipal JwtUser user,
            @RequestBody @Valid AddCartReqDto reqDto) {
        return Response.ok(cartService.addCarts(user.getEmail(), reqDto));
    }

    @GetMapping
    public Response<List<GetCartResDto>> getCarts(@AuthenticationPrincipal JwtUser user){
        return Response.ok(cartService.getCarts(user.getEmail()));
    }

    @PutMapping
    public Response<CartResDto> modifyCart(@AuthenticationPrincipal JwtUser user,
                                           @Valid @RequestBody PutCartReqDto reqDto){
        return Response.ok(cartService.putCart(user.getEmail(), reqDto));
    }

    @DeleteMapping
    public Response<CartResDto> deleteCarts(@AuthenticationPrincipal JwtUser user,
                                            @Valid @RequestBody DeleteCartReqDto reqDto){
        return Response.ok(cartService.deleteCarts(user.getEmail(), reqDto));
    }

}
