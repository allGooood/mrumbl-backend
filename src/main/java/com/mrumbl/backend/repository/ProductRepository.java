package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByInUse(boolean inUse);
    Optional<Product> findByIdAndInUse(Long productId, boolean inUse);
    List<Product> findAllByIdInAndInUse(List<Long> productIds, boolean inUse);
}
