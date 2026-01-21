package com.mrumbl.backend.repository.store;

import com.mrumbl.backend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
//    @Query("SELECT s FROM Store s " +
//            "WHERE s.isActive = true " +
//                "AND (s.storeName LIKE CONCAT('%', :keyword, '%') " +
//                    "OR s.address LIKE CONCAT('%', :keyword, '%') " +
//                    "OR s.addressDetail LIKE CONCAT('%', :keyword, '%')) " +
//            "ORDER BY s.storeName ASC")
//    List<Store> searchByKeyword(String keyword);

//    @Query("SELECT s FROM Store s " +
//            "WHERE " +
//                "ST_Distance_Sphere(POINT(:x, :y), POINT(s.xCoordinate, s.yCoordinate)) <= :r " +
//            "ORDER BY ST_Distance_Sphere(POINT(:x, :y), POINT(s.xCoordinate, s.yCoordinate)) ASC")
//    List<Store> findNearbyStores(BigDecimal x, BigDecimal y, Integer r);

    Optional<Store> findByIdAndIsActiveIsTrue(Long storeId);
    boolean existsByIdAndIsActiveIsTrue(Long storeId);

}
