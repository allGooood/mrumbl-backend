package com.mrumbl.backend.service.order;

import com.mrumbl.backend.common.enumeration.OrderState;
import com.mrumbl.backend.common.enumeration.PaymentMethod;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.OrderErrorCode;
import com.mrumbl.backend.controller.order.dto.*;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.domain.Order;
import com.mrumbl.backend.domain.OrderItem;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.order.OrderItemRepository;
import com.mrumbl.backend.repository.order.OrderRepository;
import com.mrumbl.backend.repository.product.ProductRepository;
import com.mrumbl.backend.service.cart.CartService;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import com.mrumbl.backend.service.order.mapper.OrderMapper;
import com.mrumbl.backend.service.order.validation.OrderValidator;
import com.mrumbl.backend.service.product.validation.ProductValidator;
import com.mrumbl.backend.service.store.validation.StoreValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mrumbl.backend.service.order.mapper.OrderMapper.toGetOrderDetailResDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private final CartService cartService;

    private final MemberValidator memberValidator;
    private final StoreValidator storeValidator;
    private final OrderValidator orderValidator;
    private final ProductValidator productValidator;

    @Transactional
    public AddOrderResponse addOrder(String email, AddOrderRequest reqDto) {
        // Validation
        Member memberFound = memberValidator.checkAndReturnExistingMember(email);
        Store storeFound = storeValidator.checkAndReturnStore(reqDto.getStoreId());

        // TODO - 주문 아이템 품절 체크: ProductStock을 조회하여 재고 수량 확인 및 주문 수량과 비교
        // TODO - 주문 아이템 storeId 일치 검증: 모든 아이템이 reqDto.getStoreId()와 일치하는지 확인
        // TODO - 주문 금액 검증: reqDto의 금액이 실제 상품 가격 합계와 일치하는지 검증
        List<Product> productsFound = checkAllIsInUseAndReturnProducts(reqDto.getItems());
        String orderNo = createOrderNumber();
        PaymentMethod paymentMethod = PaymentMethod.from(reqDto.getPaymentMethod());

        // Order 저장
        Order orderEntity = createOrderEntity(memberFound, storeFound, orderNo, paymentMethod, reqDto);
        Order savedOrder = orderRepository.save(orderEntity);

        // OrderItem 저장
        List<OrderItem> orderItemsEntity = createOrderItemsEntity(savedOrder, reqDto.getItems(), productsFound);
        orderItemRepository.saveAll(orderItemsEntity);

        // TODO - 재고 차감: 주문 성공 후 ProductStock.stockQuantity 차감 (낙관적/비관적 락 고려)
        
        // Cart 삭제
        // TODO - Redis 트랜잭션 이슈: Cart 삭제 실패 시 Order/OrderItem은 이미 커밋됨. 
        //       Redis 삭제를 트랜잭션 전에 수행하거나, 실패 시 보상 트랜잭션(Order 취소) 고려
        deleteCartAndCartKeyAfterOrder(email, reqDto.getCartIds());

        log.info("Order created successfully. orderNo={}, memberId={}, storeId={}", orderNo, memberFound.getId(), storeFound.getId());

        return AddOrderResponse.builder()
                .orderNo(orderNo)
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrdersResponse> getOrders(String email) {
        memberValidator.checkExistingMember(email);

        List<Order> ordersFound = orderRepository.findOrdersAndItemsByEmail(email);
        log.info("Found {} orders for email={}", ordersFound.size(), email);

        List<OrdersResponse> response = ordersFound.stream()
                .map(order -> {
                    if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                        log.warn("Order has no items. orderId={}", order.getId());
                        throw new BusinessException(OrderErrorCode.ORDER_ITEMS_NOT_FOUND);
                    }
                    return OrderMapper.toGetOrderResDto(order);
                })
                .toList();

        log.info("Successfully retrieved {} orders for email={}", response.size(), email);
        return response;
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(String email, Long orderId){
        Order orderFound = orderValidator.checkAndReturnExistingOrder(email, orderId);

        OrderDetailResponse response = toGetOrderDetailResDto(orderFound);
        log.info("Order detail built. email={}, orderId={}", email, orderId);

        return response;
    }




    private void deleteCartAndCartKeyAfterOrder(String email, Set<String> cartIds){
        if(cartIds != null && !cartIds.isEmpty()){
            cartService.deleteCartAndCartKey(email, cartIds);
            log.info("Cart deleted successfully. email={}, cartIds={}", email, cartIds);
        }
    }

    private List<Product> checkAllIsInUseAndReturnProducts(List<OrderItemDto> itemDtos) {
        var productIds = itemDtos.stream()
                .map(OrderItemDto::getProductId)
                .toList();
        return productValidator.checkAndReturnProducts(productIds);
    }

    private String createOrderNumber() {
        return "M" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + RandomStringUtils.randomAlphanumeric(10).toUpperCase();
    }

    private Order createOrderEntity(Member member, Store store, String orderNo,
                                    PaymentMethod paymentMethod, AddOrderRequest reqDto) {
        return Order.builder()
                .member(member) 
                .store(store)
                .orderNo(orderNo)
                .orderState(OrderState.PAID)
                .paymentMethod(paymentMethod)
                .productAmount(reqDto.getProductAmount())
                .taxAmount(reqDto.getTaxAmount())
                .paymentAmount(reqDto.getPaymentAmount())
                .orderedAt(LocalDateTime.now())
                .build();
    }

    private List<OrderItem> createOrderItemsEntity(Order order,
                                                   List<OrderItemDto> itemDtos,
                                                   List<Product> products) {
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        
        return itemDtos.stream()
                .map(itemDto -> {
                    Product product = productMap.get(itemDto.getProductId());

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .productNameSnapshot(itemDto.getProductName())
                            .productAmountSnapshot(itemDto.getProductAmount())
                            .options(itemDto.getOptions())
                            .quantity(itemDto.getQuantity())
                            .unitAmountSnapshot(itemDto.getUnitAmount())
                            .imageUrlSnapshot(itemDto.getImageUrl())
                            .build();
                })
                .toList();
    }

}
