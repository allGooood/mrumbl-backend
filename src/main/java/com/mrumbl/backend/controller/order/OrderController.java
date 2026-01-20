package com.mrumbl.backend.controller.order;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.order.dto.AddOrderReqDto;
import com.mrumbl.backend.controller.order.dto.GetOrderDetailResDto;
import com.mrumbl.backend.controller.order.dto.GetOrderResDto;
import com.mrumbl.backend.controller.order.dto.OrderResDto;
import com.mrumbl.backend.service.order.OrderService;
import jakarta.validation.Valid;
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
    public Response<OrderResDto> addOrder(@AuthenticationPrincipal JwtUser user,
                                          @Valid @RequestBody AddOrderReqDto reqDto) {
        return Response.ok(orderService.addOrder(user.getEmail(), reqDto));
    }

    @GetMapping
    public Response<List<GetOrderResDto>> getOrders(@AuthenticationPrincipal JwtUser user) {
        return Response.ok(orderService.getOrders(user.getEmail()));
    }

    @GetMapping("/{orderId}")
    public Response<GetOrderDetailResDto> getOrderDetail(@AuthenticationPrincipal JwtUser user,
                                                         @PathVariable Long orderId){
        return Response.ok(orderService.getOrderDetail(user.getEmail(), orderId));
    }
}
