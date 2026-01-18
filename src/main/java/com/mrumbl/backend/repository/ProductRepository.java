package com.mrumbl.backend.repository;

import com.mrumbl.backend.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByInUse(boolean inUse);
    Optional<Product> findByIdAndInUse(Long productId, boolean inUse);
}
