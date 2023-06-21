package com.coderhglee.eshop.products.application;

import org.springframework.stereotype.Service;
import com.coderhglee.eshop.products.assembler.ProductAssembler;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import com.coderhglee.eshop.products.dto.ProductDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductStockAvailabilityService implements ProductStockAvailabilityUseCase {

    private final IProductRepository productRepository;

    @Override
    public ProductDto checkAvailableForPurchase(String id, int quantity) {
        if (id == null || quantity < 0) {
            throw new IllegalArgumentException("The request is not correct.");
        }

        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("The product cannot be found."));

        product.checkAvailableForPurchase(quantity);

        return ProductAssembler.toDto(product);
    }
}
