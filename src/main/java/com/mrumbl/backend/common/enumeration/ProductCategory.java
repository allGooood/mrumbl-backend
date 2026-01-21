package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.util.EnumConverter;
import lombok.*;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    LARGE_DESSERTS("Large Desserts", 1),
    MINI_DESSERTS("Mini Desserts", 2),
    EXTRAS("Extras", 3),
    GIFT_CARDS("Gift Cards", 4);

    private final String categoryName;
    private final int displayOrder;

    public static ProductCategory from(String productCategoryStr) {
        return EnumConverter.from(productCategoryStr, ProductCategory.class, "ProductCategory");
    }
}
