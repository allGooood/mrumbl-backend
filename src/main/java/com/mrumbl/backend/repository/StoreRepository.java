package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("SELECT s FROM Store s " +
            "WHERE s.isActive = true " +
                "AND (s.storeName LIKE CONCAT('%', :keyword, '%') " +
                    "OR s.address LIKE CONCAT('%', :keyword, '%') " +
                    "OR s.addressDetail LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY s.storeName ASC") // TODO - orderBy 가까운 순서?
    List<Store> searchByKeyword(String keyword);
}
