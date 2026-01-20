package com.mrumbl.backend.controller.order;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.order.dto.AddOrderReqDto;
import com.mrumbl.backend.controller.order.dto.OrderResDto;
import com.mrumbl.backend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Response<OrderResDto> addOrder(@AuthenticationPrincipal JwtUser user,
                                          @RequestBody AddOrderReqDto reqDto){
        return Response.ok(orderService.addOrder(user.getEmail(), reqDto));
    }

}
