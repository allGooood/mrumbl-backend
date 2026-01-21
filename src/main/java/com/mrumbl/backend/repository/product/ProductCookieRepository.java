package com.mrumbl.backend.repository.product;

import com.mrumbl.backend.domain.ProductCookie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCookieRepository extends JpaRepository<ProductCookie, Long> {
    List<ProductCookie> findAllByInUse(boolean inUse);
    ProductCookie findByIdAndInUse(Long cookieId, boolean inUse);
}
