package com.coderhglee.eshop.products.application;

import com.coderhglee.eshop.products.dto.ProductDto;

public interface GetProductUseCase {
    ProductDto findById(String id);

    ProductDto findByCode(String code);
}
