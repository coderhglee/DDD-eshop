package com.coderhglee.eshop.products.assembler;

import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.dto.ProductDto;

public class ProductAssembler {
    public static ProductDto toDto(Product product) {
        return new ProductDto(product.getStringId(), product.getCode(), product.getName(),
                product.getPriceToString(), product.getQuantity());
    }
}
