package com.mrumbl.backend.controller.cart;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.cart.dto.AddCartReqDto;
import com.mrumbl.backend.controller.cart.dto.CartResDto;
import com.mrumbl.backend.controller.cart.dto.GetCartResDto;
import com.mrumbl.backend.service.CartService;
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
}
