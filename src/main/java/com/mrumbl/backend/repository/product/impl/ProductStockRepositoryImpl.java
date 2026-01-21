package com.mrumbl.backend.repository.product.impl;

import com.mrumbl.backend.domain.ProductStock;
import com.mrumbl.backend.domain.QProduct;
import com.mrumbl.backend.domain.QProductStock;
import com.mrumbl.backend.domain.QStore;
import com.mrumbl.backend.repository.product.ProductStockRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductStock> findByStoreIdWithFetchJoin(Long storeId) {
        QProductStock productStock = QProductStock.productStock;
        QStore store = QStore.store;
        QProduct product = QProduct.product;

        return jpaQueryFactory
                .select(productStock)
                .from(productStock)
                .join(productStock.store, store).fetchJoin()
                .join(productStock.product, product).fetchJoin()
                .where(store.id.eq(storeId))
                .orderBy(store.id.asc())
                .fetch();
    }

    @Override
    public Optional<ProductStock> findByStoreIdAndProductId(Long storeId, Long productId) {
        QProductStock productStock = QProductStock.productStock;
        QStore store = QStore.store;
        QProduct product = QProduct.product;

        ProductStock result = jpaQueryFactory
                .select(productStock)
                .from(productStock)
                .join(productStock.store, store).fetchJoin()
                .join(productStock.product, product).fetchJoin()
                .where(store.id.eq(storeId),
                        product.id.eq(productId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
