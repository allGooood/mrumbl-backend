package com.mrumbl.backend.repository.order.impl;

import com.mrumbl.backend.domain.Order;
import com.mrumbl.backend.domain.QMember;
import com.mrumbl.backend.domain.QOrder;
import com.mrumbl.backend.domain.QOrderItem;
import com.mrumbl.backend.domain.QProduct;
import com.mrumbl.backend.domain.QStore;
import com.mrumbl.backend.repository.order.OrderRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findOrdersAndItemsByEmail(String email) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        QStore store = QStore.store;
        QOrderItem orderItem = QOrderItem.orderItem;
        QProduct product = QProduct.product;

        return queryFactory
                .selectDistinct(order)
                .from(order)
                .join(order.member, member).fetchJoin()
                .join(order.store, store).fetchJoin()
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.product, product).fetchJoin()
                .where(member.email.eq(email))
                .orderBy(order.orderedAt.desc(), orderItem.id.asc())
                .fetch();
    }

    @Override
    public Optional<Order> findOrderAndItemsByOrderIdAndEmail(Long orderId, String email) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        QStore store = QStore.store;
        QOrderItem orderItem = QOrderItem.orderItem;
        QProduct product = QProduct.product;

        // Join으로 인한 여러개의 row 검색되는 경우 주의
        Order result = queryFactory
                .selectDistinct(order)
                .from(order)
                .join(order.member, member).fetchJoin()
                .join(order.store, store).fetchJoin()
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.product, product).fetchJoin()
                .where(member.email.eq(email), order.id.eq(orderId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
