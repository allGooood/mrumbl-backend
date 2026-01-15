package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.domain.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    @Query("SELECT ps FROM ProductStock ps " +
           "JOIN FETCH ps.store s " +
           "JOIN FETCH ps.product p " +
           "WHERE s.id = :storeId " +
           "ORDER BY s.id, p.id")
    List<ProductStock> findByStoreIdWithStoreAndProduct(@Param("storeId") Long storeId);
//    List<ProductStock> findByStoreId(Long storeId);
}
