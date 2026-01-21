package com.mrumbl.backend.repository.store.impl;

import com.mrumbl.backend.domain.QStore;
import com.mrumbl.backend.domain.Store;
import com.mrumbl.backend.repository.store.StoreRepositoryCustom;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Store> searchByKeyword(String keyword) {
        QStore store = QStore.store;
        String keywordPattern = "%" + keyword + "%";

        return jpaQueryFactory
                .select(store)
                .from(store)
                .where(store.isActive.eq(true)
                        .and(store.storeName.like(keywordPattern)
                                .or(store.address.like(keywordPattern))
                                .or(store.addressDetail.like(keywordPattern))))
                .orderBy(store.storeName.asc())
                .fetch();
    }

    @Override
    public List<Store> findNearbyStores(BigDecimal x, BigDecimal y, Integer r) {
        QStore store = QStore.store;

        // MySQL의 ST_Distance_Sphere 함수를 사용하기 위한 Template
        NumberExpression<BigDecimal> distance = Expressions.numberTemplate(
                BigDecimal.class,
                "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
                x, y, store.xCoordinate, store.yCoordinate
        );

        return jpaQueryFactory
                .select(store)
                .from(store)
                .where(distance.loe(BigDecimal.valueOf(r)))
                .orderBy(distance.asc())
                .fetch();
    }
}
