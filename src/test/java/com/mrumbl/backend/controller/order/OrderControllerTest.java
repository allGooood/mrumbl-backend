package com.mrumbl.backend.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.order.dto.*;
import com.mrumbl.backend.service.order.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private JwtUser createJwtUser() {
        return JwtUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .build();
    }

    private void setSecurityContext(JwtUser jwtUser) {
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void addOrder_Success() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        AddOrderRequest reqDto = createAddOrderReqDto();
        AddOrderResponse responseDto = AddOrderResponse.builder()
                .orderNo("M20241201ABC123")
                .build();

        given(orderService.addOrder(eq(jwtUser.getEmail()), any(AddOrderRequest.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNo").value("M20241201ABC123"));
    }

    @Test
    @DisplayName("주문 생성 실패 - 유효성 검증 실패 (items가 비어있음)")
    void addOrder_Fail_ValidationError_EmptyItems() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        AddOrderRequest reqDto = new AddOrderRequest();
        reqDto.setItems(new ArrayList<>());  // 빈 리스트
        reqDto.setStoreId(1L);
        reqDto.setPaymentMethod("CARD");
        reqDto.setProductAmount(10000);
        reqDto.setTaxAmount(1000);
        reqDto.setPaymentAmount(11000);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 유효성 검증 실패 (storeId가 null)")
    void addOrder_Fail_ValidationError_NullStoreId() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        AddOrderRequest reqDto = new AddOrderRequest();
        reqDto.setItems(createOrderItemDtos());
        reqDto.setStoreId(null);  // null
        reqDto.setPaymentMethod("CARD");
        reqDto.setProductAmount(10000);
        reqDto.setTaxAmount(1000);
        reqDto.setPaymentAmount(11000);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void getOrders_Success() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        List<OrdersResponse> responseDto = List.of(
                OrdersResponse.builder()
                        .orderId(1L)
                        .paymentAmount(11000)
                        .itemCount(2)
                        .build()
        );

        given(orderService.getOrders(jwtUser.getEmail()))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderId").value(1L))
                .andExpect(jsonPath("$.data[0].paymentAmount").value(11000));
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void getOrderDetail_Success() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        Long orderId = 1L;
        OrderDetailResponse responseDto = OrderDetailResponse.builder()
                .orderId(orderId)
                .orderNo("M20241201ABC123")
                .paymentAmount(11000)
                .build();

        given(orderService.getOrderDetail(jwtUser.getEmail(), orderId))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.orderNo").value("M20241201ABC123"));
    }

    @Test
    @DisplayName("주문 상세 조회 실패 - 유효성 검증 실패 (orderId가 음수)")
    void getOrderDetail_Fail_ValidationError_NegativeOrderId() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        Long invalidOrderId = -1L;

        // when & then
        mockMvc.perform(get("/api/orders/{orderId}", invalidOrderId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 상세 조회 실패 - 유효성 검증 실패 (orderId가 0)")
    void getOrderDetail_Fail_ValidationError_ZeroOrderId() throws Exception {
        // given
        JwtUser jwtUser = createJwtUser();
        setSecurityContext(jwtUser);
        Long invalidOrderId = 0L;

        // when & then
        mockMvc.perform(get("/api/orders/{orderId}", invalidOrderId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private AddOrderRequest createAddOrderReqDto() {
        AddOrderRequest reqDto = new AddOrderRequest();
        reqDto.setItems(createOrderItemDtos());
        reqDto.setStoreId(1L);
        reqDto.setPaymentMethod("CARD");
        reqDto.setProductAmount(10000);
        reqDto.setTaxAmount(1000);
        reqDto.setPaymentAmount(11000);
        reqDto.setCustomerRequest("문 앞에 놓아주세요");
        reqDto.setCartIds(createCartIds());
        return reqDto;
    }

    private List<OrderItemDto> createOrderItemDtos() {
        return List.of(
                OrderItemDto.builder()
                        .productId(1L)
                        .productName("테스트 상품")
                        .quantity(2)
                        .unitAmount(5000)
                        .productAmount(10000)
                        .imageUrl("https://example.com/image.jpg")
                        .build()
        );
    }

    private Set<String> createCartIds() {
        Set<String> cartIds = new HashSet<>();
        cartIds.add("cart1");
        cartIds.add("cart2");
        return cartIds;
    }
}
