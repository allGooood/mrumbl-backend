package com.mrumbl.backend.service.product;

import com.mrumbl.backend.common.enumeration.ProductCategory;
import com.mrumbl.backend.controller.product.dto.StoreProductsResponse;
import com.mrumbl.backend.domain.Product;
import com.mrumbl.backend.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.mrumbl.backend.common.enumeration.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductService의 for-loop vs Stream 성능 비교 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductService 성능 테스트")
class ProductServicePerformanceTest {

    @Autowired
    private ProductRepository productRepository;

    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성 (실제 데이터 사용 시 주석 처리)
        testProducts = productRepository.findAllByInUse(true);
        
        // 테스트 데이터가 없으면 생성
        if (testProducts.isEmpty()) {
            testProducts = createTestProducts(1000); // 1000개 상품 생성
        }
    }

    @Test
    @DisplayName("for-loop vs Stream 성능 비교")
    void comparePerformance() {
        int iterations = 50; // 반복 횟수
        int warmupIterations = 3; // 워밍업 횟수

        // 워밍업
        for (int i = 0; i < warmupIterations; i++) {
            getProductsWithForLoop(testProducts);
            getProductsWithStream(testProducts);
        }

        // for-loop 성능 측정
        long forLoopTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            getProductsWithForLoop(testProducts);

            long end = System.nanoTime();
            forLoopTotalTime += (end - start);
        }
        double forLoopAvgTime = forLoopTotalTime / (double) iterations / 1_000_000; // ms로 변환

        // Stream 성능 측정
        long streamTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            getProductsWithStream(testProducts);

            long end = System.nanoTime();
            streamTotalTime += (end - start);
        }
        double streamAvgTime = streamTotalTime / (double) iterations / 1_000_000; // ms로 변환

        // 결과 출력
        System.out.println("\n=== 성능 테스트 결과 ===");
        System.out.printf("테스트 데이터 수: %d개%n", testProducts.size());
        System.out.printf("반복 횟수: %d회%n", iterations);
        System.out.println("----------------------------------------");
        System.out.printf("for-loop 평균 시간: %.2f ms%n", forLoopAvgTime);
        System.out.printf("Stream 평균 시간: %.2f ms%n", streamAvgTime);
        System.out.println("----------------------------------------");
        
        double difference = Math.abs(forLoopAvgTime - streamAvgTime);
        double faster = Math.min(forLoopAvgTime, streamAvgTime);
        double slower = Math.max(forLoopAvgTime, streamAvgTime);
        double speedup = slower / faster;
        
        System.out.printf("차이: %.2f ms%n", difference);
        System.out.printf("빠른 방법이 %.2f배 빠름%n", speedup);
        System.out.println("========================================\n");

        // 결과 검증 (두 방법의 결과가 동일한지 확인)
        List<StoreProductsResponse> forLoopResult = getProductsWithForLoop(testProducts);
        List<StoreProductsResponse> streamResult = getProductsWithStream(testProducts);
        
        assertThat(forLoopResult.size()).isEqualTo(streamResult.size());
        // 추가 검증 로직 필요 시 작성
    }

    /**
     * for-loop 방식 (현재 구현)
     */
    private List<StoreProductsResponse> getProductsWithForLoop(List<Product> products) {
        Map<ProductCategory, List<StoreProductsResponse.StoreProductDto>> map = new java.util.HashMap<>();
        
        for (Product product : products) {
            ProductCategory category = product.getProductCategory();

            StoreProductsResponse.StoreProductDto dto = StoreProductsResponse.StoreProductDto.builder()
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .unitAmount(product.getUnitAmount())
                    .discountRate(product.getDiscountRate())
                    .isSoldOut(false)
                    .productType(String.valueOf(product.getProductType()))
                    .imageUrl(product.getImageUrl())
                    .requiredItemCount(product.getRequiredItemCount())
                    .build();

            if (map.get(category) == null || map.get(category).isEmpty()) {
                List<StoreProductsResponse.StoreProductDto> list = new ArrayList<>();
                list.add(dto);
                map.put(category, list);
            } else {
                List<StoreProductsResponse.StoreProductDto> d = map.get(category);
                d.add(dto);
            }
        }

        List<StoreProductsResponse> response = new ArrayList<>();
        for (ProductCategory key : map.keySet()) {
            StoreProductsResponse resDto = StoreProductsResponse.builder()
                    .category(key.getCategoryName())
                    .displayOrder(key.getDisplayOrder())
                    .products(map.get(key))
                    .build();
            response.add(resDto);
        }

        return response;
    }

    /**
     * Stream 방식 (비교 대상)
     */
    private List<StoreProductsResponse> getProductsWithStream(List<Product> products) {
        Map<ProductCategory, List<StoreProductsResponse.StoreProductDto>> map = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getProductCategory,
                        Collectors.mapping(
                                product -> StoreProductsResponse.StoreProductDto.builder()
                                        .productId(product.getId())
                                        .productName(product.getProductName())
                                        .unitAmount(product.getUnitAmount())
                                        .discountRate(product.getDiscountRate())
                                        .isSoldOut(false)
                                        .productType(String.valueOf(product.getProductType()))
                                        .imageUrl(product.getImageUrl())
                                        .requiredItemCount(product.getRequiredItemCount())
                                        .build(),
                                Collectors.toList()
                        )
                ));

        return map.entrySet().stream()
                .map(entry -> StoreProductsResponse.builder()
                        .category(entry.getKey().getCategoryName())
                        .displayOrder(entry.getKey().getDisplayOrder())
                        .products(entry.getValue())
                        .build())
                .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 테스트 데이터 생성 (필요 시 사용)
     */
    private List<Product> createTestProducts(int count) {
        List<Product> products = new ArrayList<>();
        ProductCategory[] categories = ProductCategory.values();
        
        for (int i = 0; i < count; i++) {
            Product product = Product.builder()
                    .productName("테스트 상품 " + i)
                    .productType(ProductType.COOKIE_BOX)
                    .productCategory(categories[i % categories.length])
                    .unitAmount(1000 + i)
                    .discountRate(BigDecimal.valueOf(0.1))
                    .imageUrl("https://example.com/image" + i + ".jpg")
                    .requiredItemCount(1)
                    .inUse(true)
                    .build();
            products.add(product);
        }
        
        return productRepository.saveAll(products);
    }
}
