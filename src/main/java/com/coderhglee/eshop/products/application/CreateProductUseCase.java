package com.coderhglee.eshop.products.application;

import com.coderhglee.eshop.products.domain.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}
