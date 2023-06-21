package com.coderhglee.eshop.products.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.coderhglee.eshop.common.Money;
import com.coderhglee.eshop.products.domain.Product;
import com.coderhglee.eshop.products.domain.repository.IProductRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateProductService implements CreateProductUseCase {

    private final IProductRepository productRepository;

    @Transactional
    @Override
    public Product execute(CreateProductCommand command) {
        command.validate();

        Product existProduct = this.productRepository.findByCode(command.getCode());

        if (existProduct != null) {
            throw new IllegalStateException("There is a product of the same product code.");
        }

        Product product = Product.of(command.getCode(), command.getName(),Money.of(command.getPrice()),
                command.getQuantity());

        this.productRepository.save(product);

        return product;
    }

}
