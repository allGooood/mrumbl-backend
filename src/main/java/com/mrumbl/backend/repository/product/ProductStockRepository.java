package com.mrumbl.backend.repository.product;

import com.mrumbl.backend.domain.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long>, ProductStockRepositoryCustom {

}
