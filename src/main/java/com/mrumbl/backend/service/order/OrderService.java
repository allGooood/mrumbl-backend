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
import com.mrumbl.backend.repository.OrderItemRepository;
import com.mrumbl.backend.repository.OrderRepository;
import com.mrumbl.backend.repository.ProductRepository;
import com.mrumbl.backend.service.cart.CartService;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import com.mrumbl.backend.service.order.mapper.OrderMapper;
import com.mrumbl.backend.service.store.validation.StoreValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Transactional
    public OrderResDto addOrder(String email, AddOrderReqDto reqDto) {
        log.info("Creating order. email={}, storeId={}, productCount={}",
                email, reqDto.getStoreId(), reqDto.getItems() != null ? reqDto.getItems().size() : 0);

        Member memberFound = memberValidator.checkAndReturnExistingMember(email);
        Store storeFound = storeValidator.checkAndReturnStore(reqDto.getStoreId());
        List<Product> productsFound = findAllByProductIds(reqDto.getItems());

        String orderNo = createOrderNumber();
        PaymentMethod paymentMethod = PaymentMethod.from(reqDto.getPaymentMethod());

        // Order 저장
        Order orderEntity = createOrder(memberFound, storeFound, orderNo, paymentMethod, reqDto);
        Order savedOrder = orderRepository.save(orderEntity);

        // OrderItem 저장
        List<OrderItem> orderItemsEntity = createOrderItems(savedOrder, reqDto.getItems(), productsFound);
        orderItemRepository.saveAll(orderItemsEntity);

        // Cart 삭제
        Set<String> cartIds = reqDto.getCartIds();
        if(cartIds != null && !cartIds.isEmpty()){
            cartService.deleteCartAndCartKey(email, cartIds);
            log.info("Cart deleted successfully. email={}, cartIds={}", email, reqDto.getCartIds());
        }

        log.info("Order created successfully. orderNo={}, memberId={}, storeId={}",
                orderNo, memberFound.getId(), storeFound.getId());

        return OrderResDto.builder()
                .orderNo(orderNo)
                .build();
    }

    @Transactional(readOnly = true)
    public List<GetOrderResDto> getOrders(String email) {
        log.info("Getting orders. email={}", email);

        memberValidator.checkExistingMember(email);

        List<Order> ordersFound = orderRepository.findOrdersAndItemsByEmail(email);
        log.info("Found {} orders for email={}", ordersFound.size(), email);

        if (ordersFound.isEmpty()) {
            log.info("No orders found for email={}", email);
            return new ArrayList<>();
        }

        List<GetOrderResDto> response = ordersFound.stream()
                .map(OrderMapper::toGetOrderResDto)
                .toList();

        log.info("Successfully retrieved {} orders for email={}", response.size(), email);
        return response;
    }

    @Transactional(readOnly = true)
    public GetOrderDetailResDto getOrderDetail(String email, Long orderId){
        log.info("Getting order detail. email={}, orderId={}", email, orderId);

        Order orderFound = orderRepository.findOrderAndItemsByOrderIdAndEmail(orderId, email)
                .orElseThrow(() -> {
                    log.warn("Order not found. email={}, orderId={}", email, orderId);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });

        GetOrderDetailResDto response = toGetOrderDetailResDto(orderFound);

        log.info("Order detail built. email={}, orderId={}", email, orderId);
        return response;
    }




    private List<Product> findAllByProductIds(List<OrderItemDto> itemDtos) {
        var productIds = itemDtos.stream()
                .map(OrderItemDto::getProductId)
                .toList();
        return productRepository.findAllByIdInAndInUse(productIds, true);
    }

    private String createOrderNumber() {
        return "C"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + RandomStringUtils.randomAlphanumeric(10).toUpperCase();
    }

    private Order createOrder(Member member, Store store, String orderNo, 
                              PaymentMethod paymentMethod, AddOrderReqDto reqDto) {
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

    private List<OrderItem> createOrderItems(Order order, 
                                            List<OrderItemDto> productDtos,
                                            List<Product> products) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (int i = 0; i < productDtos.size(); i++) {
            OrderItemDto productDto = productDtos.get(i);
            Product product = products.get(i);
            
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productNameSnapshot(productDto.getProductName())
                    .productAmountSnapshot(productDto.getProductAmount())
                    .options(productDto.getOptions())
                    .quantity(productDto.getQuantity())
                    .unitAmountSnapshot(productDto.getUnitAmount())
                    .imageUrlSnapshot(productDto.getImageUrl())
                    .build();
            
            orderItems.add(orderItem);
        }
        return orderItems;
    }

}
