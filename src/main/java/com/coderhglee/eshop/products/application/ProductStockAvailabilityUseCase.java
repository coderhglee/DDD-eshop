package com.coderhglee.eshop.products.application;

import com.coderhglee.eshop.products.dto.ProductDto;

public interface ProductStockAvailabilityUseCase {
    ProductDto checkAvailableForPurchase(String id, int quantity);
}
