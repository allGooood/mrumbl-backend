package com.mrumbl.backend.controller.order;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.order.dto.AddOrderRequest;
import com.mrumbl.backend.controller.order.dto.OrderDetailResponse;
import com.mrumbl.backend.controller.order.dto.OrdersResponse;
import com.mrumbl.backend.controller.order.dto.AddOrderResponse;
import com.mrumbl.backend.service.order.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Response<AddOrderResponse> addOrder(@AuthenticationPrincipal JwtUser user,
                                               @Valid @RequestBody AddOrderRequest reqDto) {
        return Response.ok(orderService.addOrder(user.getEmail(), reqDto));
    }

    @GetMapping
    public Response<List<OrdersResponse>> getOrders(@AuthenticationPrincipal JwtUser user) {
        return Response.ok(orderService.getOrders(user.getEmail()));
    }

    @GetMapping("/{orderId}")
    public Response<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal JwtUser user,
                                                        @PathVariable @Positive Long orderId){
        return Response.ok(orderService.getOrderDetail(user.getEmail(), orderId));
    }
}
